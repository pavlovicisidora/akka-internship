package com.project.repository

import com.project.enums.JobStatus
import com.project.model.{Job, Workspace}
import org.joda.time.DateTime

import java.util.UUID
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class JobRepository {
  private val jobs: mutable.Map[UUID, Job] = mutable.Map.empty

  def create(workspaceId: UUID, name: String, description: Option[String], status: JobStatus, dueDate: Option[DateTime]): Future[Job] = Future {
    val id = UUID.randomUUID()
    val now = DateTime.now
    val job = Job(id, workspaceId, name, description, status, dueDate, now, now)
    jobs.put(id, job)
    job
  }

  def getById(id: UUID): Future[Option[Job]] = Future(jobs.get(id))

  def getAll(): Future[List[Job]] = Future(jobs.values.toList)

  def update(id: UUID,
             name: Either[Unit, String],
             description: Either[Unit, Option[String]],
             status: Either[Unit, JobStatus],
             dueDate: Either[Unit, Option[DateTime]]
            ): Future[Option[Job]] = Future {
    jobs.get(id).map { existing =>
      val updated = existing.copy(
        name = name.getOrElse(existing.name),
        description = description.getOrElse(existing.description),
        status = status.getOrElse(existing.status),
        due_date = dueDate.getOrElse(existing.due_date),
        updated_at = DateTime.now)
      jobs.update(id, updated)
      updated
    }
  }

  def delete(id: UUID): Future[Boolean] = Future(jobs.remove(id).isDefined)

  def getByWorkspace(projectId: UUID) : Future[List[Job]] = Future {
    jobs.values.filter(_.project_id == projectId).toList
  }
}
