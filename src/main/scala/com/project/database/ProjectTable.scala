package com.project.database

import slick.jdbc.PostgresProfile.api._
import java.util.UUID
import org.joda.time.DateTime
import com.github.tototoshi.slick.PostgresJodaSupport._
import com.project.enums.ProjectStatus
import com.project.model.Project

class ProjectTable(tag: Tag) extends Table[Project](tag, "projects") {
  def id = column[UUID]("id", O.PrimaryKey)
  def workspaceId = column[UUID]("workspace_id")
  def name = column[String]("name")
  def description = column[Option[String]]("description")
  def status = column[ProjectStatus]("status")
  def createdAt = column[DateTime]("created_at")
  def updatedAt = column[DateTime]("updated_at")

  def * = (id, workspaceId, name, description, status, createdAt, updatedAt) <> (Project.tupled, Project.unapply)
}

object ProjectTable {
  val projects = TableQuery[ProjectTable]
}
