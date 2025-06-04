package com.project.protocol

import com.project.model.{JobRequestCreate, JobRequestUpdate}

import java.util.UUID

object JobProtocol {

  sealed trait JobMessage

  case class CreateJob(request: JobRequestCreate) extends JobMessage
  case class GetJob(id: UUID) extends JobMessage
  case class UpdateJob(id: UUID, request: JobRequestUpdate) extends JobMessage
  case class DeleteJob(id: UUID) extends JobMessage
  case object GetAllJobs extends JobMessage

}
