package com.project.model

import com.project.model.TriState.Unset

import java.util.UUID
import org.joda.time.DateTime

case class Workspace(
                    id: UUID,
                    name: String,
                    description: Option[String],
                    created_at: DateTime = DateTime.now(),
                    updated_at: DateTime = DateTime.now(),
                    created_by: UUID
                    )

case class WorkspaceRequestCreateRaw(
                                   name: String,
                                   description: Option[String]
                                 )

case class WorkspaceRequestCreate(
                             name: String,
                             description: Option[String],
                             created_by: UUID
                           ) {

  def toDomain : Workspace =
    Workspace(UUID.randomUUID(), name, description, DateTime.now, DateTime.now, created_by)

}

object WorkspaceRequestCreate {
  def fromRaw(raw: WorkspaceRequestCreateRaw, userId: UUID): WorkspaceRequestCreate =
    WorkspaceRequestCreate(raw.name, raw.description, userId)
}

case class WorkspaceRequestUpdate(name: Option[TriState[String]], description: Option[TriState[String]]) {

  def toDomain(workspace : Workspace) : Workspace = {
    val newName: String = name.getOrElse(TriState.Unset) match {
      case TriState.Set(value) => value
      case _ => workspace.name
    }

    val newDescription: Option[String] = description.getOrElse(TriState.Unset) match {
      case TriState.Set(value) => Some(value)
      case TriState.Null => None
      case _ => workspace.description
    }

    workspace.copy(
      name = newName,
      description = newDescription,
      updated_at = DateTime.now(),
      created_by = workspace.created_by
    )
  }

}
