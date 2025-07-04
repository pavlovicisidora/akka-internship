package com.project.service.slick

import com.project.model.{Workspace, WorkspaceRequestCreate, WorkspaceRequestUpdate}
import com.project.repository.slick.WorkspaceRepository

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class WorkspaceService(repository: WorkspaceRepository)(implicit ec: ExecutionContext) {
  def create(wsRequest: WorkspaceRequestCreate): Future[Workspace] = {
    repository.create(wsRequest.toDomain)
  }

  def getById(id: UUID): Future[Option[Workspace]] =
    repository.getById(id)

  def getAll: Future[List[Workspace]] =
    repository.getAll

  def update(id: UUID, request: WorkspaceRequestUpdate): Future[Either[String, Workspace]] = {
    repository.getById(id).flatMap {
      case Some(existing) =>
        repository.update(request.toDomain(existing))
      case None =>
        Future.successful(Left(s"Workspace with id $id not found"))
    }
  }

  def delete(id: UUID) : Future[Boolean] = repository.delete(id)

}
