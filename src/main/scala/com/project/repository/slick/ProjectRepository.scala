package com.project.repository.slick

import com.project.database.ProjectTable
import com.project.enums.ProjectStatus.Deleted
import com.project.model.Project
import org.joda.time.DateTime

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.PostgresProfile.api._


class ProjectRepository(db: Database)(implicit ec: ExecutionContext) {

  val projects = ProjectTable.projects

  def create(project: Project): Future[Project] = {
    val insertAction = for {
      _ <- projects += project
    } yield project

    db.run(insertAction)
  }

  def getById(id: UUID): Future[Option[Project]] =
    db.run(projects.filter(_.id === id).result.headOption)

  def getAll: Future[List[Project]] =
    db.run(projects.result).map(_.toList)

  def update(newProject: Project): Future[Either[String, Project]] = {
    val action = projects
      .filter(_.id === newProject.id)
      .update(newProject)
      .map {
        case 0 => Left("Unsuccessful updating project")
        case _ => Right(newProject)
      }

    db.run(action)
  }

  def delete(id: UUID): Future[Option[Project]] = {
    val query = projects.filter(_.id === id)
    val action = query.result.headOption.flatMap {
      case None =>
        DBIO.successful(None)
      case Some(existing) =>
        val deleted = existing.copy(
          status = Deleted,
          updated_at = DateTime.now()
        )
        query.update(deleted).map(_ => Some(deleted))
    }

    db.run(action.transactionally)
  }

  def getByWorkspace(workspaceId: UUID) : Future[List[Project]] = {
    db.run(projects.filter(_.workspaceId === workspaceId).result).map(_.toList)
  }

}
