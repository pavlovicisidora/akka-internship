package com.calculator.actors

import akka.actor.Actor
import com.calculator.model.{Add, Divide, Multiply, Result, Subtract}

class AddActor extends Actor {
  override def receive: Receive = {
    case Add(a,b) =>
      sender() ! Result(s"$a + $b = ${a + b}")
  }
}

class SubtractActor extends Actor {
  override def receive: Receive = {
    case Subtract(a, b) => sender() ! Result(s"$a - $b = ${a - b}")
  }
}

class MultiplyActor extends Actor {
  override def receive: Receive = {
    case Multiply(a, b) => sender() ! Result(s"$a * $b = ${a * b}")
  }
}

class DivideActor extends Actor {
  override def receive: Receive = {
    case (_, 0) => sender() ! Result("You cannot divide by zero")
    case Divide(a, b) => sender() ! Result(s"$a / $b = ${a / b}")
  }
}
