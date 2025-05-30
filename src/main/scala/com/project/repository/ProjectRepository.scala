package com.project.repository

import com.project.enums.ProjectStatus
import com.project.enums.ProjectStatus.Deleted
import com.project.model.Project
import org.joda.time.DateTime

import java.util.UUID
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class ProjectRepository()(implicit ec: ExecutionContext) {
  private val projects: mutable.Map[UUID, Project] = mutable.Map.empty

  def create(project: Project): Future[Project] = Future {
    projects.put(project.id, project)
    project
  }

  def getById(id: UUID): Future[Option[Project]] = Future(projects.get(id))

  def getAll(): Future[List[Project]] = Future(projects.values.toList)

  def update(newProject: Project): Future[Option[Project]] = Future {
    projects.get(newProject.id).map { _ =>
      projects.update(newProject.id, newProject)
      newProject
    }
  }

  def delete(id: UUID): Future[Option[Project]] = Future {
    projects.get(id).map { existing =>
      val deleted = existing.copy(
        status = Deleted,
        updated_at = DateTime.now)
      projects.update(id, deleted)
      deleted
    }
  }

  def getByWorkspace(workspaceId: UUID) : Future[List[Project]] = Future {
    projects.values.filter(_.workspace_id == workspaceId).toList
  }

}
