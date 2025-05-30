package com.project.enums

import enumeratum._

sealed trait ProjectStatus extends EnumEntry

object ProjectStatus extends Enum[ProjectStatus] {
  case object Active extends ProjectStatus
  case object Completed extends ProjectStatus
  case object Deleted extends ProjectStatus

  override val values: IndexedSeq[ProjectStatus] = findValues

  def fromString(s: String): Option[ProjectStatus] = {
    s.toLowerCase match {
      case "active" => Some(Active)
      case "completed" => Some(Completed)
      case "deleted" => Some(Deleted)
      case _ => None
    }
  }
}