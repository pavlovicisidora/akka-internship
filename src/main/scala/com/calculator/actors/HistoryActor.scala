package com.calculator.actors

import akka.actor.Actor
import com.calculator.model.{History, ShowHistory}

class HistoryActor extends Actor {
  override def receive: Receive = handleHistory(Nil)

  private def handleHistory(history: List[String]): Receive = {
    case ShowHistory =>
      if(history.isEmpty)
        println("No operations in history yet")
      sender() ! History(history)
    case entry: String =>
      val updated = history :+ entry
      context.become(handleHistory(updated))
  }
}
