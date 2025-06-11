package com.project.model

import com.project.enums.JobStatus
import org.joda.time.DateTime

import java.util.UUID

case class Job(
                id: UUID,
                projectId: UUID,
                name: String,
                description: Option[String],
                status: JobStatus,
                dueDate: Option[DateTime],
                createdAt: DateTime = DateTime.now(),
                updatedAt: DateTime = DateTime.now(),
                createdBy: UUID
              )

case class JobRequestCreateRaw(
                                projectId: UUID,
                                name: String,
                                description: Option[String],
                                dueDate: Option[DateTime]
                              )

case class JobRequestCreate(
                             projectId: UUID,
                             name: String,
                             description: Option[String],
                             dueDate: Option[DateTime],
                             createdBy: UUID
                     ) {

  def toDomain: Job =
    Job(UUID.randomUUID(), projectId, name, description, JobStatus.Pending, dueDate, DateTime.now, DateTime.now, createdBy)

}

object JobRequestCreate {
  def fromRaw(raw: JobRequestCreateRaw, userId: UUID): JobRequestCreate =
    JobRequestCreate(raw.projectId, raw.name, raw.description, raw.dueDate, userId)
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
      case _ => job.dueDate
    }

    job.copy(
      id = job.id,
      projectId = job.projectId,
      name = newName,
      description = newDescription,
      status = newStatus,
      dueDate = newDueDate,
      updatedAt = DateTime.now,
      createdBy = job.createdBy
    )
  }

}
