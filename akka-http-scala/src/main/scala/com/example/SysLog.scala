package com.example

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import scala.collection.immutable

import java.nio.file.Paths
import akka.stream.scaladsl.FileIO
import scala.io.Source
import java.io.File

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext
import org.apache.log4j.Logger
import org.apache.log4j.Level


import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.RootJsonFormat
import spray.json.DefaultJsonProtocol._


import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Dataset, Encoder, Encoders}


final case class Demo(numberOfRow: Array[String])

final case class GetSize(size: Long)
//Return System Log Data Json Format
final case class PreOReturnSysData(fromPosition: String, toPosition: String)
final case class PreReturnSysData(dateTime: String, message: String, highlightText: List[PreOReturnSysData])
final case class ReturnSysData(data: Array[String], dateTimeFrom: String, dateTimeUntil: String, searchPhrase: String)

case class DemoReturn(date: String, hour: String, min: String, message: String, fromPosition: String,toPosition: String){
   def toNested = List(PreReturnSysData(date, message, List(PreOReturnSysData(fromPosition, toPosition))))
}
//Return Histogram Data Json Format
final case class PreSysHistogram(dateTime: String, counts: Int)
final case class ReturnSysHistogram(histogram: Array[String], dateTimeFrom: String, dateTimeUntil: String, searchPhrase: String)
//Mapping Log File Data
case class log(ip:String,date:String,hour:Int,min:Int,sec:Int,methodType:String,uri:String,protocol:String,response:Int,size:Int);

object SysLog {
  //Creating Spark Context
  val conf = new SparkConf().setAppName("Log Analyzer").setMaster("local") ;
  val sc = new SparkContext(conf);
  val sqlContext = new org.apache.spark.sql.SQLContext(sc);
  import sqlContext.implicits._

  //Regex For mapping/parsing Log file data
  val logRegex = """^(\S+) - - \[(\S+):(\S+):(\S+):(\S+) -\S+] "(\S+) (\S+) (\S+)\/\S+ (\S+) (\S+)""".r;

  //Read System Log File Data
  val logFile = sc.textFile("./src/main/resources/syslog.log"); 
  
  //import sqlContext.implicits._
    
  //Filter and Mapping Logfile Data using Regex  
  val logs = logFile.filter(line=>line.matches(logRegex.toString)).map(line=>parseLog(line,logRegex)).toDF();

  val finalLog = logs.selectExpr("concat(nvl(date, '')) as date", "concat(nvl(hour, '')) as hour", "concat(nvl(min, '')) as min","concat(nvl(ip, ''),(' '), nvl(methodType, ''),(' '), nvl(uri, ''), (' '), nvl(protocol, ''),(' '), nvl(response, ''),(' '), nvl(size, '')) as message")
    
  // Helper function for mapping
  // Parses log and returns case class of the returned values.
    def parseLog(line: String,logRegex:scala.util.matching.Regex):log = {
      val logRegex(ip,date,hour,min,sec,methodType,uri,protocol,response,size) = line;
      log(ip,date,hour.toInt,min.toInt,sec.toInt,methodType,uri,protocol,response.toInt,assertInt(size));
    }

    // Function to assert if a String has integer and if not return 0 by default.
    def assertInt(variable:String):Int = {
        if(variable.forall(_.isDigit))
            return variable.toInt
        else
            return 0;
    }

  //To Get File Size
  def getFileSize() = {
    val someFile:File = new File("./src/main/resources/syslog.log")
    val fileSize = someFile.length()
    GetSize(fileSize)
  }

  
  // //To Get File Data
  def getSysData(data: SysData) = {
     //If date time converted into timestam it will be perfect
    val x = finalLog.filter((finalLog("date") >= data.dateTimeFrom ) && (finalLog("date") <= data.dateTimeUntil))
    val y = x.filter($"message".contains(data.searchPhrase))
    val strLength = (data.searchPhrase).length
    val z = y.withColumn(
              "fromPosition",
              instr($"message", "/rdiff/TWiki")
            )

    val a = z.withColumn("toPosition", z("fromPosition")+ strLength-1)

    val intermediate = a.as[DemoReturn].map(_.toNested)
    // val kafkaDf = intermediate.select(
    //   to_json(struct(
    //     $"dateTime".as("dateTime"), 
    //     $"message".as("message"),  
    //     $"highlightText".as("highlightText")
    //   )))

    ReturnSysData(intermediate.toJSON.collect(), data.dateTimeFrom, data.dateTimeUntil, data.searchPhrase)
  }

  // //To Get Histogram Data
  def getHistogramData(histogram: SysHistogram) = {
    //If date time converted into timestam it will be perfect
    val x = finalLog.filter((finalLog("date") >= histogram.dateTimeFrom) && (finalLog("date") <= histogram.dateTimeUntil))
    val y = x.filter($"message".contains(histogram.searchPhrase))
    var preResult = y.select($"date".as("dateTime")).groupBy("dateTime").count()
    ReturnSysHistogram(preResult.toJSON.collect(), histogram.dateTimeFrom, histogram.dateTimeUntil, histogram.searchPhrase)
    
  }

}
