package com.project.repository.scalikejdbc

import com.project.model.Workspace
import org.joda.time.DateTime
import scalikejdbc._

import java.util.UUID

object WorkspaceRepository extends SQLSyntaxSupport[Workspace] {

  implicit val dateTimeBinder: TypeBinder[DateTime] =
    TypeBinder.timestamp.map(ts => new DateTime(ts.getTime))

  def apply(rs: WrappedResultSet): Workspace = Workspace(
    id = UUID.fromString(rs.string("id")),
    name = rs.string("name"),
    description = rs.stringOpt("description"),
    created_at = rs.get[DateTime]("created_at"),
    updated_at = rs.get[DateTime]("updated_at"),
  )

  def create(workspace: Workspace)(implicit session: DBSession = AutoSession): Workspace = {
    sql"""
         INSERT INTO workspaces(id, name, description, created_at, updated_at)
         VALUES (${workspace.id}, ${workspace.name}, ${workspace.description}, ${workspace.created_at},
         ${workspace.updated_at})
       """.update.apply()
    workspace
  }

  def getById(id: UUID)(implicit session: DBSession = ReadOnlyAutoSession): Option[Workspace] = {
    sql"""SELECT * FROM workspaces WHERE id = $id"""
      .map(rs => apply(rs)).single.apply()
  }

  def getAll()(implicit session: DBSession = ReadOnlyAutoSession): List[Workspace] = {
    sql"SELECT * FROM workspaces".map(apply).list.apply()
  }

  def update(workspace: Workspace)(implicit session: DBSession = AutoSession): Option[Workspace] = {
    val rows = sql"""
        UPDATE workspaces SET
          name = ${workspace.name},
          description = ${workspace.description},
          created_at = ${workspace.created_at},
          updated_at = ${workspace.updated_at}
        WHERE id = ${workspace.id}
      """.update.apply()
      if(rows > 0) Some(workspace) else None
  }

  def delete(id: UUID)(implicit session: DBSession = AutoSession): Boolean = {
    sql"DELETE FROM workspaces WHERE id = $id".update.apply() > 0
  }

}
