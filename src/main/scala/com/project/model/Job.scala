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
              updated_at: DateTime = DateTime.now(),
              created_by: UUID
              )

case class JobRequestCreateRaw(
                                project_id: UUID,
                                name: String,
                                description: Option[String],
                                due_date: Option[DateTime]
                              )

case class JobRequestCreate(
                       project_id: UUID,
                       name: String,
                       description: Option[String],
                       due_date: Option[DateTime],
                       created_by: UUID
                     ) {

  def toDomain: Job =
    Job(UUID.randomUUID(), project_id, name, description, JobStatus.Pending, due_date, DateTime.now, DateTime.now, created_by)

}

object JobRequestCreate {
  def fromRaw(raw: JobRequestCreateRaw, userId: UUID): JobRequestCreate =
    JobRequestCreate(raw.project_id, raw.name, raw.description, raw.due_date, userId)
}

case class JobRequestUpdate(
                       name: Option[String],
                       description: Option[TriState[String]],
                       status: Option[JobStatus],
                       due_date: Option[TriState[DateTime]]
                     ) {

  def toDomain(job: Job) : Job = {
    val newName = name.getOrElse(job.name)

    val newDescription: Option[String] = description.getOrElse(TriState.Unset) match {
      case TriState.Set(value) => Some(value)
      case TriState.Null => None
      case _ => job.description
    }

    val newStatus = status.getOrElse(job.status)

    val newDueDate: Option[DateTime] = due_date.getOrElse(TriState.Unset) match {
      case TriState.Set(value) => Some(value)
      case TriState.Null => None
      case _ => job.due_date
    }

    job.copy(
      id = job.id,
      project_id = job.project_id,
      name = newName,
      description = newDescription,
      status = newStatus,
      due_date = newDueDate,
      updated_at = DateTime.now,
      created_by = job.created_by
    )
  }

}
