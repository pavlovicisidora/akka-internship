package com.project.enums

import enumeratum._

sealed trait ProjectStatus extends EnumEntry

object ProjectStatus extends Enum[ProjectStatus] {
  case object Active extends ProjectStatus
  case object Completed extends ProjectStatus
  case object Deleted extends ProjectStatus

  override val values: IndexedSeq[ProjectStatus] = findValues

  def fromString(s: String): ProjectStatus = {
    s.toLowerCase match {
      case "active" => Active
      case "completed" => Completed
      case "deleted" => Deleted
      case other       => throw new IllegalArgumentException(s"Unknown status: $other")
    }
  }

  def toString(status: ProjectStatus): String = status match {
    case Active    => "Active"
    case Completed => "Completed"
    case Deleted    => "Deleted"
    case other       => throw new IllegalArgumentException(s"Unknown status: $other")
  }

  import slick.jdbc.PostgresProfile.api._
  implicit val projectStatusColumnType: BaseColumnType[ProjectStatus] =
    MappedColumnType.base[ProjectStatus, String](
      toString, fromString
    )
}
