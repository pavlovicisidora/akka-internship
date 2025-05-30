package com.calculator.actors

import akka.actor.{Actor, ActorRef, Terminated}
import com.calculator.model._

class HistoryActor extends Actor {
  override def receive: Receive = handleHistory(Nil, Set.empty)

  private def handleHistory(history: List[String], subscribers: Set[ActorRef]): Receive = {
    case ShowHistory =>
      if(history.isEmpty)
        println("No operations in history yet")
      sender() ! History(history)
    case entry: String =>
      val updatedHistory = history :+ entry
      subscribers.foreach(_ ! entry)
      context.become(handleHistory(updatedHistory, subscribers))

    case Subscribe =>
      context.watch(sender())
      history.foreach(sender() ! _)
      context.become(handleHistory(history, subscribers + sender()))

    case Unsubscribe =>
      context.become(handleHistory(history, subscribers - sender()))

    case Terminated(ref) =>
      context.become(handleHistory(history, subscribers - ref))
  }
}
