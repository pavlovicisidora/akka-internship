package com.project.service.scalikejdbc

import com.project.model.{Project, ProjectRequestCreate, ProjectRequestUpdate}
import com.project.repository.scalikejdbc.{ProjectRepository, WorkspaceRepository}
import com.project.repository.slick.{ProjectRepository, WorkspaceRepository}
import scalikejdbc.{AutoSession, DBSession, ReadOnlyAutoSession}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class ProjectService()(implicit ec: ExecutionContext) {

  def create(pRequest: ProjectRequestCreate)(implicit session: DBSession = AutoSession): Future[Option[Project]] = Future {
    WorkspaceRepository.getById(pRequest.workspace_id) match {
      case Some(_) => Some(ProjectRepository.create(pRequest.toDomain))
      case None => None
    }
  }

  def getById(id: UUID)(implicit session: DBSession = ReadOnlyAutoSession): Future[Option[Project]] =
    Future(ProjectRepository.getById(id))

  def getAll()(implicit session: DBSession = ReadOnlyAutoSession): Future[List[Project]] =
    Future(ProjectRepository.getAll)

  def update(id: UUID, request: ProjectRequestUpdate)(implicit session: DBSession = AutoSession): Future[Option[Project]] = Future {
    ProjectRepository.getById(id) match {
      case Some(existing) => ProjectRepository.update(request.toDomain(existing))
      case None => None
    }
  }

  def delete(id: UUID)(implicit session: DBSession = AutoSession): Future[Boolean] = Future(ProjectRepository.delete(id))

}
