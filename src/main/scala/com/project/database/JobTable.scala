package com.project.database

import java.util.UUID
import org.joda.time.DateTime
import com.github.tototoshi.slick.PostgresJodaSupport._
import com.project.enums.JobStatus
import com.project.model.Job
import slick.jdbc.PostgresProfile.api._

class JobTable(tag: Tag) extends Table[Job](tag, "jobs") {
  def id = column[UUID]("id", O.PrimaryKey)
  def projectId = column[UUID]("project_id")
  def name = column[String]("name")
  def description = column[Option[String]]("description")
  def status = column[JobStatus]("status")
  def dueDate = column[Option[DateTime]]("due_date")
  def createdAt = column[DateTime]("created_at")
  def updatedAt = column[DateTime]("updated_at")

  def * = (id, projectId, name, description, status, dueDate, createdAt, updatedAt) <> (Job.tupled, Job.unapply)
}

object JobTable {
  val jobs = TableQuery[JobTable]
}