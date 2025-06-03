package com.project.repository.scalikejdbc

import scalikejdbc._
import org.joda.time.DateTime
import java.util.UUID
import com.project.model.Job
import com.project.enums.JobStatus

object JobRepository extends SQLSyntaxSupport[Job] {

  implicit val jobStatusBinder: TypeBinder[JobStatus] =
    TypeBinder.string.map(JobStatus.fromString)

  implicit val dateTimeBinder: TypeBinder[DateTime] =
    TypeBinder.timestamp.map(ts => new DateTime(ts.getTime))

  implicit val dateTimeOptBinder: TypeBinder[Option[DateTime]] =
    TypeBinder.timestamp.map(ts =>
      Option(ts).map(t => new DateTime(t.getTime))
    )

  def apply(rs: WrappedResultSet): Job = Job(
    id = UUID.fromString(rs.string("id")),
    project_id = UUID.fromString(rs.string("project_id")),
    name = rs.string("name"),
    description = rs.stringOpt("description"),
    status = rs.get[JobStatus]("status"),
    due_date = rs.get[Option[DateTime]]("due_date"),
    created_at = rs.get[DateTime]("created_at"),
    updated_at = rs.get[DateTime]("updated_at")
  )

  def create(job: Job)(implicit session: DBSession = AutoSession): Job = {
    sql"""
      INSERT INTO jobs (id, project_id, name, description, status, due_date, created_at, updated_at)
      VALUES (${job.id}, ${job.project_id}, ${job.name}, ${job.description}, ${job.status.toString},
              ${job.due_date}, ${job.created_at}, ${job.updated_at})
    """.update.apply()
    job
  }

  def getById(id: UUID)(implicit session: DBSession = ReadOnlyAutoSession): Option[Job] = {
    sql"SELECT * FROM jobs WHERE id = $id"
      .map(rs => apply(rs)).single.apply()
  }

  def getAll()(implicit session: DBSession = ReadOnlyAutoSession): List[Job] = {
    sql"SELECT * FROM jobs".map(apply).list.apply()
  }

  def update(job: Job)(implicit session: DBSession = AutoSession): Option[Job] = {
    val rows = sql"""
      UPDATE jobs SET
        project_id = ${job.project_id},
        name = ${job.name},
        description = ${job.description},
        status = ${job.status.toString},
        due_date = ${job.due_date},
        created_at = ${job.created_at},
        updated_at = ${job.updated_at}
      WHERE id = ${job.id}
    """.update.apply()
    if (rows > 0) Some(job) else None
  }

  def delete(id: UUID)(implicit session: DBSession = AutoSession): Boolean = {
    sql"DELETE FROM jobs WHERE id = $id".update.apply() > 0
  }

  def getByProject(projectId: UUID)(implicit session: DBSession = ReadOnlyAutoSession): List[Job] = {
    sql"SELECT * FROM jobs WHERE project_id = $projectId".map(apply).list.apply()
  }
}
