package com.project.service.slick

import com.project.model.{Job, JobRequestCreate, JobRequestUpdate}
import com.project.repository.slick.{JobRepository, ProjectRepository}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class JobService(jobRepository: JobRepository, projectRepository: ProjectRepository)(implicit ec: ExecutionContext) {

  def create(jRequest: JobRequestCreate): Future[Option[Job]] = {
    projectRepository.getById(jRequest.project_id). flatMap {
      case Some(_) => jobRepository.create(jRequest.toDomain).map(Some(_))
      case None => Future.successful(None)
    }
  }

  def getById(id: UUID): Future[Option[Job]] =
    jobRepository.getById(id)

  def getAll: Future[List[Job]] =
    jobRepository.getAll

  def update(id: UUID, request: JobRequestUpdate): Future[Option[Job]] = {
    jobRepository.getById(id).flatMap {
      case Some(existing) => jobRepository.update(request.toDomain(existing))
      case None => Future.successful(None)
    }
  }

  def delete(id: UUID) : Future[Boolean] = jobRepository.delete(id)

}
