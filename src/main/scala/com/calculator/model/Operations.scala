package com.calculator.model

sealed trait Operations
case class Add(a: Double, b: Double) extends Operations
case class Subtract(a: Double, b: Double) extends Operations
case class Multiply(a: Double, b: Double) extends Operations
case class Divide(a: Double, b: Double) extends Operations

case object ShowHistory
case class Result(output: String)
case class History(entries: List[String])
