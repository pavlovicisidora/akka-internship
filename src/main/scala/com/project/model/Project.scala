package com.project.model

import com.project.enums.ProjectStatus
import org.joda.time.DateTime

import java.util.UUID

case class Project(
                    id: UUID,
                    workspace_id: UUID,
                    name: String,
                    description: Option[String],
                    status: ProjectStatus,
                    created_at: DateTime = DateTime.now(),
                    updated_at: DateTime = DateTime.now(),
                    created_by: UUID
                  )

case class ProjectRequestCreateRaw(
                                    workspace_id: UUID,
                                    name: String,
                                    description: Option[String]
                                  )

case class ProjectRequestCreate(
                           workspace_id: UUID,
                           name: String,
                           description: Option[String],
                           created_by: UUID
                         ) {

  def toDomain : Project =
    Project(UUID.randomUUID(), workspace_id, name, description, ProjectStatus.Active, DateTime.now, DateTime.now, created_by)

}

object ProjectRequestCreate {
  def fromRaw(raw: ProjectRequestCreateRaw, userId: UUID): ProjectRequestCreate =
    ProjectRequestCreate(raw.workspace_id, raw.name, raw.description, userId)
}

case class ProjectRequestUpdate(
                                 name: Option[String],
                                 description: Option[TriState[String]],
                                 status: Option[ProjectStatus]
                               ) {

  def toDomain(project : Project) : Project = {
    val newName = name.getOrElse(project.name)

    val newDescription: Option[String] = description.getOrElse(TriState.Unset) match {
      case TriState.Set(value) => Some(value)
      case TriState.Null => None
      case _ => project.description
    }

    val newStatus = status.getOrElse(project.status)

    project.copy(
      id = project.id,
      workspace_id = project.workspace_id,
      name = newName,
      description = newDescription,
      status = newStatus,
      created_at = project.created_at,
      updated_at = DateTime.now(),
      created_by = project.created_by
    )
  }

}
