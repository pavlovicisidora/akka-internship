package com.project.repository

import com.project.model.Workspace
import org.joda.time.DateTime

import java.util.UUID
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WorkspaceRepository {
  private val workspaces: mutable.Map[UUID, Workspace] = mutable.Map.empty

  def create(name: String, description: Option[String]): Future[Workspace] = Future {
    val id = UUID.randomUUID()
    val now = DateTime.now
    val ws = Workspace(id, name, description, now, now)
    workspaces.put(id, ws)
    ws
  }

  def getById(id: UUID): Future[Option[Workspace]] = Future(workspaces.get(id))

  def getAll(): Future[List[Workspace]] = Future(workspaces.values.toList)

  def update(id: UUID,
             name: Either[Unit, String],
             description: Either[Unit, Option[String]]
            ): Future[Option[Workspace]] = Future {
    workspaces.get(id).map { existing =>
      val updated = existing.copy(
        name = name.getOrElse(existing.name),
        description = description.getOrElse(existing.description),
        updated_at = DateTime.now)
      workspaces.update(id, updated)
      updated
    }
  }

  def delete(id: UUID): Future[Boolean] = Future(workspaces.remove(id).isDefined)

}
