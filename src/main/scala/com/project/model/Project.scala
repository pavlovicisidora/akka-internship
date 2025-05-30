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
                    updated_at: DateTime = DateTime.now()
                  )

case class ProjectRequestCreate(
                           workspace_id: UUID,
                           name: String,
                           description: Option[String]
                         ) {

  def toDomain : Project =
    Project(UUID.randomUUID(), workspace_id, name, description, ProjectStatus.Active, DateTime.now, DateTime.now)

}

case class ProjectRequestUpdate(
                                 name: Option[String],
                                 description: Either[Unit, Option[String]],
                                 status: Option[ProjectStatus]
                               ) {

  def toDomain(project : Project) : Project = {
    val newName = name.getOrElse(project.name)

    val newDescription = description match {
      case Left(_)         => project.description
      case Right(newValue) => newValue
    }

    val newStatus = status.getOrElse(project.status)

    project.copy(
      id = project.id,
      workspace_id = project.workspace_id,
      name = newName,
      description = newDescription,
      status = newStatus,
      created_at = project.created_at,
      updated_at = DateTime.now()
    )
  }

}
