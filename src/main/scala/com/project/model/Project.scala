package com.project.model

import com.project.enums.ProjectStatus
import org.joda.time.DateTime

import java.util.UUID

case class Project(
                    id: UUID,
                    workspaceId: UUID,
                    name: String,
                    description: Option[String],
                    status: ProjectStatus,
                    createdAt: DateTime = DateTime.now(),
                    updatedAt: DateTime = DateTime.now(),
                    createdBy: UUID
                  )

case class ProjectRequestCreateRaw(
                                    workspaceId: UUID,
                                    name: String,
                                    description: Option[String]
                                  )

case class ProjectRequestCreate(
                           workspaceId: UUID,
                           name: String,
                           description: Option[String],
                           createdBy: UUID
                         ) {

  def toDomain : Project =
    Project(UUID.randomUUID(), workspaceId, name, description, ProjectStatus.Active, DateTime.now, DateTime.now, createdBy)

}

object ProjectRequestCreate {
  def fromRaw(raw: ProjectRequestCreateRaw, userId: UUID): ProjectRequestCreate =
    ProjectRequestCreate(raw.workspaceId, raw.name, raw.description, userId)
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
      workspaceId = project.workspaceId,
      name = newName,
      description = newDescription,
      status = newStatus,
      createdAt = project.createdAt,
      updatedAt = DateTime.now(),
      createdBy = project.createdBy
    )
  }

}
