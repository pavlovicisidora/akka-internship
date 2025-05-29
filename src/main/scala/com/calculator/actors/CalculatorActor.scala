package com.calculator.actors

import akka.actor.{Actor, ActorRef}
import akka.pattern.ask
import akka.util.Timeout
import com.calculator.model._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
class CalculatorActor(addActor: ActorRef, subtractActor: ActorRef, multiplyActor: ActorRef, divideActor: ActorRef, historyActor: ActorRef) extends Actor {
  implicit val timeout: Timeout = Timeout(3.seconds)
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
    case ShowHistory =>
      val replyTo = sender()
      (historyActor ? ShowHistory).mapTo[History].foreach(replyTo ! _)

    case SubscribeToHistory(subscriber) =>
      historyActor.tell(Subscribe, subscriber)
    case UnsubscribeFromHistory(subscriber) =>
      historyActor.tell(Unsubscribe, subscriber)
  }
}
