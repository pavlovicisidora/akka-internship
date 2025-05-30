package com.project.enums

import enumeratum._
import enumeratum.EnumEntry._

sealed trait ProjectStatus extends EnumEntry

object ProjectStatus extends Enum[ProjectStatus] {
  case object Active extends ProjectStatus
  case object Completed extends ProjectStatus
  case object Deleted extends ProjectStatus

  override val values: IndexedSeq[ProjectStatus] = findValues
}