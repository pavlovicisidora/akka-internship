package com.project.repository.scalikejdbc

import com.project.enums.ProjectStatus
import com.project.model.Project
import org.joda.time.DateTime
import scalikejdbc.{AutoSession, DBSession, ReadOnlyAutoSession, SQLSyntaxSupport, TypeBinder, WrappedResultSet, scalikejdbcSQLInterpolationImplicitDef}

import java.util.UUID

object ProjectRepository extends SQLSyntaxSupport[Project] {

  implicit val jobStatusBinder: TypeBinder[ProjectStatus] =
    TypeBinder.string.map(ProjectStatus.fromString)

  implicit val dateTimeBinder: TypeBinder[DateTime] =
    TypeBinder.timestamp.map(ts => new DateTime(ts.getTime))

  def apply(rs: WrappedResultSet): Project = Project(
    id = UUID.fromString(rs.string("id")),
    workspace_id = UUID.fromString(rs.string("workspace_id")),
    name = rs.string("name"),
    description = rs.stringOpt("description"),
    status = rs.get[ProjectStatus]("status"),
    created_at = rs.get[DateTime]("created_at"),
    updated_at = rs.get[DateTime]("updated_at")
  )

  def create(project: Project)(implicit session: DBSession = AutoSession): Project = {
    sql"""
         INSERT INTO projects (id, workspace_id, name, description, status,
                                 created_at, updated_at)
         VALUES (${project.id}, ${project.workspace_id}, ${project.name},
                 ${project.description}, ${ProjectStatus.toString(project.status)}, ${project.created_at},
                 ${project.updated_at})
       """.update.apply()
    project
  }

  def getById(id: UUID)(implicit session: DBSession = ReadOnlyAutoSession) : Option[Project] = {
    sql"SELECT * FROM projects WHERE id = $id"
      .map(apply).single.apply()
  }

  def getAll()(implicit session: DBSession = ReadOnlyAutoSession) : List[Project] = {
    sql"SELECT * FROM projects"
      .map(apply).list.apply()
  }

  def update(project: Project)(implicit session: DBSession = AutoSession): Option[Project] = {
    val rows = sql"""
      UPDATE projects SET
        workspace_id = ${project.workspace_id},
        name = ${project.name},
        description = ${project.description},
        status = ${ProjectStatus.toString(project.status)},
        created_at = ${project.created_at},
        updated_at = ${project.updated_at}
      WHERE id = ${project.id}
    """.update.apply()
    if (rows > 0) Some(project) else None
  }

  def delete(id: UUID)(implicit session: DBSession = AutoSession): Boolean = {
    val now = DateTime.now
    val rows = sql"""
      UPDATE projects SET
        status = ${ProjectStatus.Deleted.toString},
        updated_at = $now
      WHERE id = $id
    """.update.apply()
    if (rows > 0) true else false
  }

  def getByWorkspace(workspaceId: UUID)(implicit session: DBSession = ReadOnlyAutoSession): List[Project] = {
    sql"SELECT * FROM projects WHERE project_id = $workspaceId".map(apply).list.apply()
  }

}
