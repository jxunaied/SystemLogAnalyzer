package com.example

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

import scala.concurrent.Future
import com.example.SysLog
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout

final case class Status(status: String)
//final case class SysData(dateTimeFrom: String, dateTimeUntil: String, searchPhrase: String)
//final case class SysHistogram(dateTimeFrom: String, dateTimeUntil: String, searchPhrase: String)

trait SyslogRoutes {

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._
  
  val routes: Route =
    pathPrefix("api") {
      concat(
        pathEnd {
          concat(
            get {
              complete(s"check")
            })
        }~ path("get_status") {      
                  get {        
                    complete{
                      Status(s"Ok")
                    }
                  } 
          }~ path("get_size") {      
                  get {        
                    complete{
                       SysLog.getFileSize()
                    }
                  } 
            }~ path("data") {      
                  post {    
                    entity(as[SysData]) { data =>
                      complete(SysLog.getSysData(data))
                    }    
                  } 
            }~ path("histogram") {      
                  post {        
                    entity(as[SysHistogram]) { histogram =>
                      complete(SysLog.getHistogramData(histogram))
                    }  
                  } 
            }
        )
    }
}
