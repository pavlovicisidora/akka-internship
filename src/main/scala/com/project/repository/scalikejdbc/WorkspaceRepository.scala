package com.project.repository.scalikejdbc

import com.project.model.Workspace
import org.joda.time.DateTime
import scalikejdbc._

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class WorkspaceRepository()(implicit ec: ExecutionContext) extends SQLSyntaxSupport[Workspace] {

  implicit val dateTimeBinder: TypeBinder[DateTime] =
    TypeBinder.timestamp.map(ts => new DateTime(ts.getTime))

  def apply(rs: WrappedResultSet): Workspace = Workspace(
    id = UUID.fromString(rs.string("id")),
    name = rs.string("name"),
    description = rs.stringOpt("description"),
    created_at = rs.get[DateTime]("created_at"),
    updated_at = rs.get[DateTime]("updated_at"),
    created_by = UUID.fromString(rs.string("created_by"))
  )

  def create(workspace: Workspace)(implicit session: DBSession = AutoSession): Future[Workspace] = Future {
    sql"""
         INSERT INTO workspaces(id, name, description, created_at, updated_at, created_by)
         VALUES (${workspace.id}, ${workspace.name}, ${workspace.description}, ${workspace.created_at},
         ${workspace.updated_at}, ${workspace.created_by})
       """.update.apply()
    workspace
  }

  def getById(id: UUID)(implicit session: DBSession = ReadOnlyAutoSession): Future[Option[Workspace]] = Future {
    sql"""SELECT * FROM workspaces WHERE id = $id"""
      .map(rs => apply(rs)).single.apply()
  }

  def getAll()(implicit session: DBSession = ReadOnlyAutoSession): Future[List[Workspace]] = Future {
    sql"SELECT * FROM workspaces".map(apply).list.apply()
  }

  def update(workspace: Workspace)(implicit session: DBSession = AutoSession): Future[Option[Workspace]] = Future {
    val rows = sql"""
        UPDATE workspaces SET
          name = ${workspace.name},
          description = ${workspace.description},
          updated_at = ${workspace.updated_at}
        WHERE id = ${workspace.id}
      """.update.apply()
      if(rows > 0) Some(workspace) else None
  }

  def delete(id: UUID)(implicit session: DBSession = AutoSession): Future[Boolean] = Future {
    sql"DELETE FROM workspaces WHERE id = $id".update.apply() > 0
  }

}
