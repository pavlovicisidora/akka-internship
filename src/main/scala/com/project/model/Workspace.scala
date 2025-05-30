package com.project.model

import java.util.UUID
import org.joda.time.DateTime

case class Workspace(
                    id: UUID,
                    name: String,
                    description: Option[String],
                    created_at: DateTime = DateTime.now(),
                    updated_at: DateTime = DateTime.now()
                    )

case class WorkspaceRequestCreate(
                             name: String,
                             description: Option[String]
                           ) {
  def toDomain : Workspace =
    Workspace(UUID.randomUUID(), name, description, DateTime.now, DateTime.now)
}

case class WorkspaceRequestUpdate(name: Option[String], description: Either[Unit, Option[String]]) {
  def toDomain(workspace : Workspace) : Workspace = {
    val newName = name.getOrElse(workspace.name)

    val newDescription = description match {
      case Left(_)         => workspace.description
      case Right(newValue) => newValue
    }

    workspace.copy(
      name = newName,
      description = newDescription,
      updated_at = DateTime.now()
    )
  }
}
