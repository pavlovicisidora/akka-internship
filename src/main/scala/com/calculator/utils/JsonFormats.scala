package com.calculator.utils

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.calculator.model._
import spray.json.{DefaultJsonProtocol, JsValue, RootJsonFormat, enrichAny}

trait JsonFormats extends DefaultJsonProtocol {
  implicit val addFormat: RootJsonFormat[Add] = jsonFormat2(Add)
  implicit val subFormat: RootJsonFormat[Subtract] = jsonFormat2(Subtract)
  implicit val mulFormat: RootJsonFormat[Multiply] = jsonFormat2(Multiply)
  implicit val divFormat: RootJsonFormat[Divide] = jsonFormat2(Divide)
  implicit val resultFormat: RootJsonFormat[Result] = jsonFormat1(Result)
  implicit val historyFormat: RootJsonFormat[History] = jsonFormat1(History)
}
