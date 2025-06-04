package com.project.service.scalikejdbc

import com.project.model.{Job, JobRequestCreate, JobRequestUpdate}
import com.project.repository.scalikejdbc.{JobRepository, ProjectRepository}
import scalikejdbc.{AutoSession, DBSession, ReadOnlyAutoSession}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class JobService()(implicit ec: ExecutionContext) {
  val projectRepository = new ProjectRepository()
  val jobRepository = new JobRepository()

  def create(jRequest: JobRequestCreate)(implicit session: DBSession = AutoSession): Future[Option[Job]] = {
    projectRepository.getById(jRequest.project_id).flatMap {
      case Some(_) => jobRepository.create(jRequest.toDomain).map(Some(_))
      case None => Future.successful(None)
    }
  }

  def getById(id: UUID)(implicit session: DBSession = ReadOnlyAutoSession): Future[Option[Job]] =
    jobRepository.getById(id)

  def getAll()(implicit session: DBSession = ReadOnlyAutoSession): Future[List[Job]] =
    jobRepository.getAll

  def update(id: UUID, request: JobRequestUpdate)(implicit session: DBSession = AutoSession): Future[Option[Job]] = {
    jobRepository.getById(id).flatMap {
      case Some(existing) => jobRepository.update(request.toDomain(existing))
      case None => Future.successful(None)
    }
  }

  def delete(id: UUID)(implicit session: DBSession = AutoSession): Future[Boolean] = jobRepository.delete(id)

}
