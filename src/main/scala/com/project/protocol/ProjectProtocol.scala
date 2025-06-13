package com.project.protocol

import com.project.model.{ProjectRequestCreate, ProjectRequestUpdate}

import java.util.UUID

object ProjectProtocol {

  sealed trait ProjectMessage

  case class CreateProject(request: ProjectRequestCreate) extends ProjectMessage
  case class GetProject(id: UUID) extends ProjectMessage
  case class UpdateProject(id: UUID, request: ProjectRequestUpdate) extends ProjectMessage
  case class DeleteProject(id: UUID) extends ProjectMessage
  case object GetAllProjects extends ProjectMessage

}
