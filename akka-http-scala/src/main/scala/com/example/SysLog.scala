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
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.DataFrame


final case class Demo(numberOfRow: String)

final case class GetSize(size: Long)
final case class SysData(dateTimeFrom: String, dateTimeUntil: String, searchPhrase: String)
final case class SysHistogram(dateTimeFrom: String, dateTimeUntil: String, searchPhrase: String)
//Return System Log Data Json Format
final case class PreOReturnSysData(fromPosition: Int, toPosition: Int)
final case class PreReturnSysData(dataTime: String, message: String, highlightText: PreOReturnSysData)
final case class ReturnSysData(data: PreReturnSysData, dateTimeFrom: String, dateTimeUntil: String, searchPhrase: String)
//Return Histogram Data Json Format
final case class PreSysHistogram(dateTime: String, counts: Int)
final case class ReturnSysHistogram(data: PreSysHistogram, dateTimeFrom: String, dateTimeUntil: String, searchPhrase: String)
//Mapping Log File Data
case class log(month:String, date:String, hour:String,min:String,sec:String, serverName:String, pid:String, response:String)

object SysLog {
  //Creating Spark Context
  val conf = new SparkConf().setAppName("sysLog").setMaster("local[*]")
  val sc = new SparkContext(conf)

  //Regex For mapping/parsing Log file data
  val logRegex = """^(\S+) (\S+) (\S+):(\S+):(\S+) (\S+) (\S+): (\S+)""".r;

  //Read System Log File Data
  val logFile = sc.textFile("./src/main/resources/syslog.log"); 
  
  //import spark.implicits._
    
  //Filter and Mapping Logfile Data using Regex  
  val logs = logFile.filter(line=>line.matches(logRegex.toString)).map(line=>parseLog(line,logRegex));
    
  // Helper function for mapping
  def parseLog(line: String,logRegex:scala.util.matching.Regex):log = {
    val logRegex(month,date,hour,min,sec,serverName,pid,response) = line;
    log(month,date,hour,min,sec,serverName,pid,response);
  }

  //To Get File Size
  def getFileSize() = {
    val someFile:File = new File("./src/main/resources/syslog.log")
    val fileSize = someFile.length()
    GetSize(fileSize)
  }

  // //To Get File Data
  def getSysData(data: SysData) = {
    //Filter System Log Data
    // val logFileHistogram = logFile.filter(df("date") >= "Jan" 
    //                                    && df("date") <= "Feb"
    //                                    && df("pid") === "Error")
    val x = logFile.first();

    //Not Completed Thats why Demo Data Returning
    Demo(x)
  }

  // //To Get Histogram Data
  def getHistogramData(histogram: SysHistogram) = {
    //Filter Histogram Data
    // val logFileHistogram = logFile.filter(df("date") >= "Jan" 
    //                                    && df("date") <= "Feb"
    //                                    && df("pid") === "Error")
  
    val counts = logFile.count();
    val firstData = logFile.first();
    
    //Not Completed Thats why Demo Data Returning
    Demo(firstData)
  }

}
