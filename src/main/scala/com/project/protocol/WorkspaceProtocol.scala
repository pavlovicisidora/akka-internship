package com.project.protocol

import com.project.model.{WorkspaceRequestCreate, WorkspaceRequestUpdate}

import java.util.UUID

object WorkspaceProtocol {

  sealed trait WorkspaceMessage

  case class CreateWorkspace(request: WorkspaceRequestCreate) extends WorkspaceMessage
  case class GetWorkspace(id: UUID) extends WorkspaceMessage
  case class UpdateWorkspace(id: UUID, request: WorkspaceRequestUpdate) extends WorkspaceMessage
  case class DeleteWorkspace(id: UUID) extends WorkspaceMessage
  case object GetAllWorkspaces extends WorkspaceMessage

}
