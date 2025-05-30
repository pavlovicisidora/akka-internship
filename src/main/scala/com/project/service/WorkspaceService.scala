package com.project.service

import com.project.model.{Workspace, WorkspaceRequestCreate, WorkspaceRequestUpdate}
import com.project.repository.WorkspaceRepository
import org.joda.time.DateTime

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class WorkspaceService(repository: WorkspaceRepository)(implicit ec: ExecutionContext) {
  def create(wsRequest: WorkspaceRequestCreate): Future[Workspace] = {
    repository.create(wsRequest.toDomain)
  }

  def getById(id: UUID): Future[Option[Workspace]] =
    repository.getById(id)


  def getAll: Future[List[Workspace]] =
    repository.getAll()

  def update(id: UUID, request: WorkspaceRequestUpdate): Future[Option[Workspace]] = {
    repository.getById(id).flatMap {
      case Some(existing) => repository.update(request.toDomain(existing))
      case None => Future.successful(None)
    }
  }

  def delete(id: UUID) : Future[Boolean] = repository.delete(id)

}
