package com.project.repository

import com.project.model.Job

import java.util.UUID
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class JobRepository()(implicit ec: ExecutionContext) {

  private val jobs: mutable.Map[UUID, Job] = mutable.Map.empty

  def create(job: Job): Future[Job] = Future {
    jobs.put(job.id, job)
    job
  }

  def getById(id: UUID): Future[Option[Job]] = Future(jobs.get(id))

  def getAll: Future[List[Job]] = Future(jobs.values.toList)

  def update(newJob: Job): Future[Option[Job]] = Future {
    jobs.get(newJob.id).map { _ =>
      jobs.update(newJob.id, newJob)
      newJob
    }
  }

  def delete(id: UUID): Future[Boolean] = Future(jobs.remove(id).isDefined)

  def getByWorkspace(projectId: UUID) : Future[List[Job]] = Future {
    jobs.values.filter(_.project_id == projectId).toList
  }

}
