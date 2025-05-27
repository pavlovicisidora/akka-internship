package com.calculator.actors

import akka.actor.Actor

class HistoryActor extends Actor {
  override def receive: Receive = handleHistory(Nil)

  private def handleHistory(history: List[String]): Receive = {
    case "show" if history.isEmpty => println("No operations in history yet")
    case "show" =>
      println("History:")
      history.foreach(println)
    case entry: String =>
      val updated = history :+ entry
      context.become(handleHistory(updated))
  }
}
