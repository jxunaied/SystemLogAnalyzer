package com.example

import spray.json.DefaultJsonProtocol

object JsonFormats  {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val demoJsonFormat = jsonFormat1(Demo)
  implicit val statusJsonFormat = jsonFormat1(Status)
  implicit val sizeJsonFormat = jsonFormat1(GetSize)
  implicit val sysDatasonFormat = jsonFormat3(SysData)
  implicit val sysHistogramJsonFormat = jsonFormat3(SysHistogram)
}
