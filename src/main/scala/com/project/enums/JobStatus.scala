package com.project.enums

import enumeratum.{Enum, EnumEntry}

sealed trait JobStatus extends EnumEntry

object JobStatus extends Enum[JobStatus] {
  case object Pending extends JobStatus
  case object InProgress extends JobStatus
  case object Done extends JobStatus

  override val values: IndexedSeq[JobStatus] = findValues
}
