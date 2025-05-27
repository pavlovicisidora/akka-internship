package com.calculator

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.calculator.actors.{AddActor, CalculatorActor, DivideActor, HistoryActor, MultiplyActor, SubtractActor}
import com.calculator.model.{Add, Divide, Multiply, Result, ShowHistory, Subtract}

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

object Main extends App {
  implicit val timeout: Timeout = Timeout(3.seconds)
  @tailrec
  private def readLoop(): Unit = {
    val input = scala.io.StdIn.readLine("> ").trim.toLowerCase().split(" ").toList
    val operation = input match {
      case "exit" :: Nil =>
        println("Exiting...")
        calculatorSystem.terminate()
        sys.exit()
        None
      case "show" :: Nil =>
        Some(ShowHistory)
      case a :: "+" :: b :: Nil if a.forall(_.isDigit) && b.forall(_.isDigit) =>
        Some(Add(a.toDouble, b.toDouble))
      case a :: "-" :: b :: Nil if a.forall(_.isDigit) && b.forall(_.isDigit) =>
        Some(Subtract(a.toDouble, b.toDouble))
      case a :: "*" :: b :: Nil if a.forall(_.isDigit) && b.forall(_.isDigit) =>
        Some(Multiply(a.toDouble, b.toDouble))
      case a :: "/" :: b :: Nil if a.forall(_.isDigit) && b.forall(_.isDigit) =>
        Some(Divide(a.toDouble, b.toDouble))
      case _ =>
        println("Invalid input")
        None
    }
    operation.foreach{ op =>
      val future = calculator ? op
      future.mapTo[Result].foreach(result => println(result.output))
    }
    readLoop()
  }

  val calculatorSystem = ActorSystem("CalculatorSystem")
  val add = calculatorSystem.actorOf(Props[AddActor])
  val sub = calculatorSystem.actorOf(Props[SubtractActor])
  val mul = calculatorSystem.actorOf(Props[MultiplyActor])
  val div = calculatorSystem.actorOf(Props[DivideActor])
  val history = calculatorSystem.actorOf(Props[HistoryActor])

  val calculator = calculatorSystem.actorOf(Props(new CalculatorActor(add, sub, mul, div, history)))

  readLoop()
}
