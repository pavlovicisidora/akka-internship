package com.project.service.scalikejdbc

import com.project.model.{Workspace, WorkspaceRequestCreate, WorkspaceRequestUpdate}
import com.project.repository.scalikejdbc.WorkspaceRepository
import scalikejdbc.{AutoSession, DBSession, ReadOnlyAutoSession}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class WorkspaceService()(implicit ec: ExecutionContext) {
  val workspaceRepository = new WorkspaceRepository()

  def create(wsRequest: WorkspaceRequestCreate)(implicit session: DBSession = AutoSession): Future[Workspace] = {
    workspaceRepository.create(wsRequest.toDomain)
  }

  def getById(id: UUID)(implicit session: DBSession = ReadOnlyAutoSession): Future[Option[Workspace]] =
    workspaceRepository.getById(id)

  def getAll()(implicit session: DBSession = ReadOnlyAutoSession): Future[List[Workspace]] =
    workspaceRepository.getAll

  def update(id: UUID, request: WorkspaceRequestUpdate)(implicit session: DBSession = AutoSession): Future[Option[Workspace]] = {
    workspaceRepository.getById(id).flatMap {
      case Some(existing) => workspaceRepository.update(request.toDomain(existing))
      case None => Future.successful(None)
    }
  }

  def delete(id: UUID)(implicit session: DBSession = AutoSession): Future[Boolean] = workspaceRepository.delete(id)

}
