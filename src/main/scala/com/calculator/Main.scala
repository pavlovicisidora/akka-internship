package com.calculator

import com.calculator.actors._
import com.calculator.model._
import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import utils.InputParser

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

object Main extends App {
  implicit val timeout: Timeout = Timeout(3.seconds)
  @tailrec
  private def readLoop(): Unit = {
    val input = scala.io.StdIn.readLine("> ")
    val operation = InputParser.parse(input) match {
      case Some("exit") =>
        println("Exiting...")
        calculatorSystem.terminate()
        sys.exit()
      case Some(ShowHistory) =>
        Some(ShowHistory)
      case Some(op: Operations) =>
        Some(op)
      case _ =>
        println("Invalid input")
        None
    }
    operation.foreach {
      case op: Operations =>
        val future = calculator ? op
        future.mapTo[Result].foreach(result => println(result.output))
      case op =>
        val future = calculator ? op
        future.mapTo[History].foreach(hist => hist.entries.foreach(println))
    }
    readLoop()
  }

  private val calculatorSystem = ActorSystem("CalculatorSystem")
  private val add = calculatorSystem.actorOf(Props[AddActor])
  private val sub = calculatorSystem.actorOf(Props[SubtractActor])
  private val mul = calculatorSystem.actorOf(Props[MultiplyActor])
  private val div = calculatorSystem.actorOf(Props[DivideActor])
  val history = calculatorSystem.actorOf(Props[HistoryActor])

  val calculator = calculatorSystem.actorOf(Props(new CalculatorActor(add, sub, mul, div, history)))

  readLoop()
}
