package com.project.service.scalikejdbc

import com.project.model.{Project, ProjectRequestCreate, ProjectRequestUpdate}
import com.project.repository.scalikejdbc.{ProjectRepository, WorkspaceRepository}
import scalikejdbc.{AutoSession, DBSession, ReadOnlyAutoSession}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class ProjectService()(implicit ec: ExecutionContext) {
  val workspaceRepository = new WorkspaceRepository()
  val projectRepository = new ProjectRepository()

  def create(pRequest: ProjectRequestCreate)(implicit session: DBSession = AutoSession): Future[Option[Project]] = {
    workspaceRepository.getById(pRequest.workspace_id).flatMap {
      case Some(_) => projectRepository.create(pRequest.toDomain).map(Some(_))
      case None => Future.successful(None)
    }
  }

  def getById(id: UUID)(implicit session: DBSession = ReadOnlyAutoSession): Future[Option[Project]] =
    projectRepository.getById(id)

  def getAll()(implicit session: DBSession = ReadOnlyAutoSession): Future[List[Project]] =
    projectRepository.getAll

  def update(id: UUID, request: ProjectRequestUpdate)(implicit session: DBSession = AutoSession): Future[Option[Project]] = {
    projectRepository.getById(id).flatMap {
      case Some(existing) => projectRepository.update(request.toDomain(existing))
      case None => Future.successful(None)
    }
  }

  def delete(id: UUID)(implicit session: DBSession = AutoSession): Future[Boolean] = projectRepository.delete(id)

}
