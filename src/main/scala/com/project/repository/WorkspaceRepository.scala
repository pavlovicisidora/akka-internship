package com.project.repository

import com.project.model.Workspace

import java.util.UUID
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class WorkspaceRepository()(implicit ec: ExecutionContext) {

  private val workspaces: mutable.Map[UUID, Workspace] = mutable.Map.empty

  def create(workspace: Workspace): Future[Workspace] = Future {
    workspaces.put(workspace.id, workspace)
    workspace
  }

  def getById(id: UUID): Future[Option[Workspace]] = Future(workspaces.get(id))

  def getAll: Future[List[Workspace]] = Future(workspaces.values.toList)

  def update(newWorkspace: Workspace): Future[Option[Workspace]] = Future {
    workspaces.get(newWorkspace.id).map { _ =>
      workspaces.update(newWorkspace.id, newWorkspace)
      newWorkspace
    }
  }

  def delete(id: UUID): Future[Boolean] = Future(workspaces.remove(id).isDefined)

}
