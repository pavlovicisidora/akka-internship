package com.calculator.utils

import com.calculator.model._

object InputParser {
  def parse(input: String): Option[Any] = {
    input.trim.toLowerCase().split(" ").toList match {
      case "exit" :: Nil =>
        Some("exit")
      case "show" :: Nil =>
        Some(ShowHistory)
      case a :: op :: b :: Nil =>
        for {
          aNum <- a.toDoubleOption
          bNum <- b.toDoubleOption
          operation <- getOperation(op, aNum, bNum)
        } yield operation
      case _ =>
        None
    }
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
}
