package com.project.service.scalikejdbc

import com.project.model.{Workspace, WorkspaceRequestCreate, WorkspaceRequestUpdate}
import com.project.repository.scalikejdbc.WorkspaceRepository
import com.project.repository.slick.WorkspaceRepository
import scalikejdbc.{AutoSession, DBSession, ReadOnlyAutoSession}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class WorkspaceService()(implicit ec: ExecutionContext) {
  def create(wsRequest: WorkspaceRequestCreate)(implicit session: DBSession = AutoSession): Future[Workspace] = Future {
    WorkspaceRepository.create(wsRequest.toDomain)
  }

  def getById(id: UUID)(implicit session: DBSession = ReadOnlyAutoSession): Future[Option[Workspace]] =
    Future(WorkspaceRepository.getById(id))

  def getAll()(implicit session: DBSession = ReadOnlyAutoSession): Future[List[Workspace]] =
    Future(WorkspaceRepository.getAll)

  def update(id: UUID, request: WorkspaceRequestUpdate)(implicit session: DBSession = AutoSession): Future[Option[Workspace]] = Future {
    WorkspaceRepository.getById(id) match {
      case Some(existing) => WorkspaceRepository.update(request.toDomain(existing))
      case None => None
    }
  }

  def delete(id: UUID)(implicit session: DBSession = AutoSession): Future[Boolean] = Future(WorkspaceRepository.delete(id))

}
