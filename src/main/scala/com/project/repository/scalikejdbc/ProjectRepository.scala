package com.project.repository.scalikejdbc

import com.project.enums.ProjectStatus
import com.project.model.Project
import org.joda.time.DateTime
import scalikejdbc.{AutoSession, DBSession, ReadOnlyAutoSession, SQLSyntaxSupport, TypeBinder, WrappedResultSet, scalikejdbcSQLInterpolationImplicitDef}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class ProjectRepository()(implicit ec: ExecutionContext) extends SQLSyntaxSupport[Project] {

  implicit val jobStatusBinder: TypeBinder[ProjectStatus] =
    TypeBinder.string.map(ProjectStatus.fromString)

  implicit val dateTimeBinder: TypeBinder[DateTime] =
    TypeBinder.timestamp.map(ts => new DateTime(ts.getTime))

  def apply(rs: WrappedResultSet): Project = Project(
    id = UUID.fromString(rs.string("id")),
    workspaceId = UUID.fromString(rs.string("workspace_id")),
    name = rs.string("name"),
    description = rs.stringOpt("description"),
    status = rs.get[ProjectStatus]("status"),
    createdAt = rs.get[DateTime]("created_at"),
    updatedAt = rs.get[DateTime]("updated_at"),
    createdBy = UUID.fromString(rs.string("created_by"))
  )

  def create(project: Project)(implicit session: DBSession = AutoSession): Future[Project] = Future {
    sql"""
         INSERT INTO projects (id, workspace_id, name, description, status,
                                 created_at, updated_at, created_by)
         VALUES (${project.id}, ${project.workspaceId}, ${project.name},
                 ${project.description}, ${ProjectStatus.toString(project.status)}, ${project.createdAt},
                 ${project.updatedAt}, ${project.createdBy})
       """.update.apply()
    project
  }

  def getById(id: UUID)(implicit session: DBSession = ReadOnlyAutoSession) : Future[Option[Project]] = Future {
    sql"SELECT * FROM projects WHERE id = $id"
      .map(apply).single.apply()
  }

  def getAll()(implicit session: DBSession = ReadOnlyAutoSession) : Future[List[Project]] = Future {
    sql"SELECT * FROM projects"
      .map(apply).list.apply()
  }

  def update(project: Project)(implicit session: DBSession = AutoSession): Future[Option[Project]] = Future {
    val rows = sql"""
      UPDATE projects SET
        name = ${project.name},
        description = ${project.description},
        status = ${ProjectStatus.toString(project.status)},
        updated_at = ${project.updatedAt}
      WHERE id = ${project.id}
    """.update.apply()
    if (rows > 0) Some(project) else None
  }

  def delete(id: UUID)(implicit session: DBSession = AutoSession): Future[Boolean] = Future {
    val now = DateTime.now
    val rows = sql"""
      UPDATE projects SET
        status = ${ProjectStatus.Deleted.toString},
        updated_at = $now
      WHERE id = $id
    """.update.apply()
    if (rows > 0) true else false
  }

  def getByWorkspace(workspaceId: UUID)(implicit session: DBSession = ReadOnlyAutoSession): Future[List[Project]] = Future {
    sql"SELECT * FROM projects WHERE workspaceId = $workspaceId".map(apply).list.apply()
  }

}
