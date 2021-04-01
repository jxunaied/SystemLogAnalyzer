package com.example

import spray.json.DefaultJsonProtocol

object JsonFormats  {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val demoJsonFormat = jsonFormat1(Demo)
  implicit val demoJsonFormat1 = jsonFormat6(DemoReturn)
  implicit val statusJsonFormat = jsonFormat1(Status)
  implicit val sizeJsonFormat = jsonFormat1(GetSize)
  implicit val sysDatasonFormat = jsonFormat4(ReturnSysData)
  implicit val sysHistogramJsonFormat = jsonFormat3(SysHistogram)
  implicit val sysHistogramJsonFormat1 = jsonFormat2(PreOReturnSysData)
  implicit val sysHistogramJsonFormat11 = jsonFormat3(PreReturnSysData)
  implicit val sysHistogramJsonFormat2 = jsonFormat3(SysData)
  implicit val sysHistogramJsonFormat23= jsonFormat4(ReturnSysHistogram)
  implicit val sysHistogramJsonFormat24 = jsonFormat2(PreSysHistogram)
}
