package com.project.service.scalikejdbc

import com.project.model.{Job, JobRequestCreate, JobRequestUpdate}
import com.project.repository.scalikejdbc.{JobRepository, ProjectRepository}
import scalikejdbc.{AutoSession, DBSession, ReadOnlyAutoSession}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class JobService()(implicit ec: ExecutionContext) {
  def create(jRequest: JobRequestCreate)(implicit session: DBSession = AutoSession): Future[Option[Job]] = Future {
    ProjectRepository.getById(jRequest.project_id) match {
      case Some(_) => Some(JobRepository.create(jRequest.toDomain))
      case None => None
    }
  }

  def getById(id: UUID)(implicit session: DBSession = ReadOnlyAutoSession): Future[Option[Job]] =
    Future(JobRepository.getById(id))

  def getAll()(implicit session: DBSession = ReadOnlyAutoSession): Future[List[Job]] =
    Future(JobRepository.getAll)

  def update(id: UUID, request: JobRequestUpdate)(implicit session: DBSession = AutoSession): Future[Option[Job]] = Future {
    JobRepository.getById(id) match {
      case Some(existing) => JobRepository.update(request.toDomain(existing))
      case None => None
    }
  }

  def delete(id: UUID)(implicit session: DBSession = AutoSession): Future[Boolean] = Future(JobRepository.delete(id))

}
