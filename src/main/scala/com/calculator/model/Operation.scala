package com.calculator.model

import akka.actor.ActorRef

sealed trait Operation
case class Add(a: Double, b: Double) extends Operation
case class Subtract(a: Double, b: Double) extends Operation
case class Multiply(a: Double, b: Double) extends Operation
case class Divide(a: Double, b: Double) extends Operation

case object ShowHistory
case object Subscribe
case object Unsubscribe

case class SubscribeToHistory(subscriber: ActorRef)
case class UnsubscribeFromHistory(subscriber: ActorRef)
case class Result(output: String)
case class History(entries: List[String])

case object Exit
