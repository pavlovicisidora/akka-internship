package com.calculator.api

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.util.Timeout
import com.calculator.actors.{AddActor, CalculatorActor, DivideActor, HistoryActor, MultiplyActor, SubtractActor}
import com.calculator.routes.CalculatorRoutes

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt

object HttpServer extends App {
  implicit val system: ActorSystem = ActorSystem("CalculatorSystem")
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val timeout: Timeout = Timeout(3.seconds)

  val add = system.actorOf(Props[AddActor])
  val sub = system.actorOf(Props[SubtractActor])
  val mul = system.actorOf(Props[MultiplyActor])
  val div = system.actorOf(Props[DivideActor])
  val history = system.actorOf(Props[HistoryActor])

  val calculator = system.actorOf(Props(new CalculatorActor(add, sub, mul, div, history)))
  val routes = new CalculatorRoutes(calculator).routes

  Http().newServerAt("localhost", 8080).bind(routes)
}
