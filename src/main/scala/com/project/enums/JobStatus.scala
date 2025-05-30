package com.project.enums

import enumeratum.{Enum, EnumEntry}

sealed trait JobStatus extends EnumEntry

object JobStatus extends Enum[JobStatus] {
  case object Pending extends JobStatus
  case object InProgress extends JobStatus
  case object Done extends JobStatus

  override val values: IndexedSeq[JobStatus] = findValues

  def fromString(s: String): Option[JobStatus] = {
    s.toLowerCase match {
      case "pending" => Some(Pending)
      case "inprogress" => Some(InProgress)
      case "done" => Some(Done)
      case _ => None
    }
  }
}
