package com.project.model

import com.project.enums.JobStatus
import org.joda.time.DateTime

import java.util.UUID

case class Job(
              id: UUID,
              project_id: UUID,
              name: String,
              description: Option[String],
              status: JobStatus,
              due_date: Option[DateTime],
              created_at: DateTime = DateTime.now(),
              updated_at: DateTime = DateTime.now()
              )

case class JobRequestCreate(
                       project_id: UUID,
                       name: String,
                       description: Option[String],
                       due_date: Option[DateTime]
                     ) {

  def toDomain: Job =
    Job(UUID.randomUUID(), project_id, name, description, JobStatus.Pending, due_date, DateTime.now, DateTime.now)

}

case class JobRequestUpdate(
                       name: Option[String],
                       description: Either[Unit, Option[String]],
                       status: Option[JobStatus],
                       due_date: Either[Unit, Option[DateTime]]
                     ) {

  def toDomain(job: Job) : Job = {
    val newName = name.getOrElse(job.name)

    val newDescription = description match {
      case Left(_)         => job.description
      case Right(newValue) => newValue
    }

    val newStatus = status.getOrElse(job.status)

    val newDueDate = due_date match {
      case Left(_)         => job.due_date
      case Right(newValue) => newValue
    }

    job.copy(
      id = job.id,
      project_id = job.project_id,
      name = newName,
      description = newDescription,
      status = newStatus,
      due_date = newDueDate,
      updated_at = DateTime.now
    )
  }

}
