package com.project.repository.slick

import com.project.database.WorkspaceTable
import com.project.model.Workspace
import slick.jdbc.PostgresProfile.api._

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class WorkspaceRepository(db: Database)(implicit ec: ExecutionContext) {

  val table = WorkspaceTable.workspaces

  def create(workspace: Workspace): Future[Workspace] = {
    val insertAction = for {
      _ <- table += workspace
    } yield workspace

    db.run(insertAction)
  }

  def getById(id: UUID): Future[Option[Workspace]] =
    db.run(table.filter(_.id === id).result.headOption)

  def getAll: Future[List[Workspace]] =
    db.run(table.result).map(_.toList)

  def update(newWorkspace: Workspace): Future[Option[Workspace]] = {
    val action = table
      .filter(_.id === newWorkspace.id)
      .update(newWorkspace)
      .map {
        case 0 => None
        case _ => Some(newWorkspace)
      }

    db.run(action)
  }

  def delete(id: UUID): Future[Boolean] =
    db.run(table.filter(_.id === id).delete.map(_ > 0))

}
