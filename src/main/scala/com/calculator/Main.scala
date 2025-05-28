package com.calculator

import com.calculator.actors._
import com.calculator.model._
import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

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
      case a :: op :: b :: Nil =>
        for {
          aNum <- a.toDoubleOption
          bNum <- b.toDoubleOption
        } yield getOperation(op, aNum, bNum).get
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

  private def getOperation(op: String, a: Double, b: Double): Option[Operations] = {
    op match {
      case "+" => Some(Add(a, b))
      case "-" => Some(Subtract(a, b))
      case "*" => Some(Multiply(a, b))
      case "/" => Some(Divide(a, b))
      case _ => None
    }
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
