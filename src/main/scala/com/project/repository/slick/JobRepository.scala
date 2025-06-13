package com.project.repository.slick

import com.project.database.JobTable
import com.project.model.Job

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.PostgresProfile.api._

class JobRepository(db: Database)(implicit ec: ExecutionContext) {

  val jobs = JobTable.jobs

  def create(job: Job): Future[Job] = {
    val insertAction = for {
      _ <- jobs += job
    } yield job

    db.run(insertAction)
  }

  def getById(id: UUID): Future[Option[Job]] =
    db.run(jobs.filter(_.id === id).result.headOption)

  def getAll: Future[List[Job]] =
    db.run(jobs.result).map(_.toList)

  def update(newJob: Job): Future[Either[String, Job]] = {
    val action = jobs
      .filter(_.id === newJob.id)
      .update(newJob)
      .map {
        case 0 => Left("Unsuccessful updating job")
        case _ => Right(newJob)
      }

    db.run(action)
  }

  def delete(id: UUID): Future[Boolean] =
    db.run(jobs.filter(_.id === id).delete.map(_ > 0))

  def getByProject(projectId: UUID) : Future[List[Job]] = {
    db.run(jobs.filter(_.projectId === projectId).result).map(_.toList)
  }

}
