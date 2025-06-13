package com.project.enums

import enumeratum._

sealed trait JobStatus extends EnumEntry

object JobStatus extends Enum[JobStatus] {
  case object Pending extends JobStatus
  case object InProgress extends JobStatus
  case object Done extends JobStatus

  override val values: IndexedSeq[JobStatus] = findValues

  def fromString(s: String): JobStatus = {
    s.toLowerCase.filterNot(_.isWhitespace) match {
      case "pending" => Pending
      case "inprogress" => InProgress
      case "done" => Done
      case other       => throw new IllegalArgumentException(s"Unknown status: $other")
    }
  }

  def toString(status: JobStatus): String = status match {
    case Pending    => "Pending"
    case InProgress => "In progress"
    case Done    => "Done"
    case other       => throw new IllegalArgumentException(s"Unknown status: $other")
  }

  import slick.jdbc.PostgresProfile.api._
  implicit val projectStatusColumnType: BaseColumnType[JobStatus] =
    MappedColumnType.base[JobStatus, String](
      toString, fromString
    )
}
