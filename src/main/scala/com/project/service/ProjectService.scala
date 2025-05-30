package com.project.service

import com.project.model.{Project, ProjectRequestCreate, ProjectRequestUpdate}
import com.project.repository.{ProjectRepository, WorkspaceRepository}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class ProjectService(projectRepository: ProjectRepository, workspaceRepository: WorkspaceRepository)(implicit ec: ExecutionContext) {

  def create(pRequest: ProjectRequestCreate): Future[Option[Project]] = {
    workspaceRepository.getById(pRequest.workspace_id).flatMap {
      case Some(_) => projectRepository.create(pRequest.toDomain).map(Some(_))
      case None => Future.successful(None)
    }
  }

  def getById(id: UUID): Future[Option[Project]] =
    projectRepository.getById(id)

  def getAll: Future[List[Project]] =
    projectRepository.getAll

  def update(id: UUID, request: ProjectRequestUpdate): Future[Option[Project]] = {
    projectRepository.getById(id).flatMap {
      case Some(existing) => projectRepository.update(request.toDomain(existing))
      case None => Future.successful(None)
    }
  }

  def delete(id: UUID) : Future[Option[Project]] = projectRepository.delete(id)

}
