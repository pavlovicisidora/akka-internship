package com.project.database

import slick.jdbc.PostgresProfile.api._
import java.util.UUID
import org.joda.time.DateTime
import com.github.tototoshi.slick.PostgresJodaSupport._
import com.project.model.Workspace

class WorkspaceTable(tag: Tag) extends Table[Workspace](tag, "workspaces") {
  def id = column[UUID]("id", O.PrimaryKey)
  def name = column[String]("name")
  def description = column[Option[String]]("description")
  def createdAt = column[DateTime]("created_at")
  def updatedAt = column[DateTime]("updated_at")
  def createdBy = column[UUID]("created_by")

  def * = (id, name, description, createdAt, updatedAt, createdBy) <> (Workspace.tupled, Workspace.unapply)
}

object WorkspaceTable {
  val workspaces = TableQuery[WorkspaceTable]
}
