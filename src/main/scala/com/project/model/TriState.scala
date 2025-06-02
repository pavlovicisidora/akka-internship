package com.project.model

sealed trait TriState[+A]
object TriState {
  case object Unset extends TriState[Nothing]
  case object Null extends TriState[Nothing]
  case class Set[A](value: A) extends TriState[A]
}