package com.project.repository

import com.project.enums.ProjectStatus
import com.project.model.Project
import org.joda.time.DateTime

import java.util.UUID
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ProjectRepository {
  private val projects: mutable.Map[UUID, Project] = mutable.Map.empty

  def create(workspaceId: UUID, name: String, description: Option[String], status: ProjectStatus): Future[Project] = Future {
    val id = UUID.randomUUID()
    val now = DateTime.now
    val project = Project(id, workspaceId, name, description, status, now, now)
    projects.put(id, project)
    project
  }

  def getById(id: UUID): Future[Option[Project]] = Future(projects.get(id))

  def getAll(): Future[List[Project]] = Future(projects.values.toList)

  def update(id: UUID,
             name: Either[Unit, String],
             description: Either[Unit, Option[String]],
             status: Either[Unit, ProjectStatus]
            ): Future[Option[Project]] = Future {
    projects.get(id).map { existing =>
      val updated = existing.copy(
        name = name.getOrElse(existing.name),
        description = description.getOrElse(existing.description),
        status = status.getOrElse(existing.status),
        updated_at = DateTime.now)
      projects.update(id, updated)
      updated
    }
  }

  def delete(id: UUID): Future[Boolean] = Future(projects.remove(id).isDefined)

  def getByWorkspace(workspaceId: UUID) : Future[List[Project]] = Future {
    projects.values.filter(_.workspace_id == workspaceId).toList
  }
}
