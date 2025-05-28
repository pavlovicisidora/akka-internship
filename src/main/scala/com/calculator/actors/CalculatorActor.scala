package com.calculator.actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.{Actor, ActorRef}
import akka.pattern.ask
import com.calculator.Main.timeout
import com.calculator.model._

import scala.concurrent.ExecutionContext.Implicits.global

class CalculatorActor(addActor: ActorRef, subtractActor: ActorRef, multiplyActor: ActorRef, divideActor: ActorRef, historyActor: ActorRef) extends Actor {
  override def receive: Receive = {
    case op: Operations =>
      val replyTo = sender()
      val operationFuture = op match {
        case _: Add => (addActor ? op).mapTo[Result]
        case _: Subtract => (subtractActor ? op).mapTo[Result]
        case _: Multiply => (multiplyActor ? op).mapTo[Result]
        case _: Divide => (divideActor ? op).mapTo[Result]
      }
      operationFuture.foreach{ res =>
        historyActor ! res.output
        replyTo ! res
      }
    case ShowHistory => historyActor ! ("show")
  }
}
