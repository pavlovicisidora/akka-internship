package com.project.repository

import com.project.database.JobTable
import com.project.model.Job

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.PostgresProfile.api._

class JobRepository(db: Database)(implicit ec: ExecutionContext) {

  val table = JobTable.jobs

  def create(job: Job): Future[Job] = {
    val insertAction = for {
      _ <- table += job
    } yield job

    db.run(insertAction)
  }

  def getById(id: UUID): Future[Option[Job]] =
    db.run(table.filter(_.id === id).result.headOption)

  def getAll: Future[List[Job]] =
    db.run(table.result).map(_.toList)

  def update(newJob: Job): Future[Option[Job]] = {
    val action = table
      .filter(_.id === newJob.id)
      .update(newJob)
      .map {
        case 0 => None
        case _ => Some(newJob)
      }

    db.run(action)
  }

  def delete(id: UUID): Future[Boolean] =
    db.run(table.filter(_.id === id).delete.map(_ > 0))

  def getByWorkspace(projectId: UUID) : Future[List[Job]] = {
    db.run(table.filter(_.projectId === projectId).result).map(_.toList)
  }

}
