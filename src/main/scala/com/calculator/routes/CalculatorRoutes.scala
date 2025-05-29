package com.calculator.routes

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.calculator.model._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import com.calculator.utils.JsonFormats

class CalculatorRoutes(calculator: ActorRef) (implicit val timeout: Timeout) extends JsonFormats {
  val routes: Route =
    path("add") {
      post {
        entity(as[Add]) {
          add =>
            complete((calculator ? add).mapTo[Result])
        }
      }
    } ~
      path("subtract") {
        post {
          entity(as[Subtract]) {
            sub =>
              complete((calculator ? sub).mapTo[Result])
          }
        }
      } ~
      path("multiply") {
        post {
          entity(as[Multiply]) {
            mul =>
              complete((calculator ? mul).mapTo[Result])
          }
        }
      } ~
      path("divide") {
        post {
          entity(as[Divide]) {
            div =>
              complete((calculator ? div).mapTo[Result])
          }
        }
      } ~
      path("history") {
        get {
          complete((calculator ? ShowHistory).mapTo[History])
        }
      }
}
