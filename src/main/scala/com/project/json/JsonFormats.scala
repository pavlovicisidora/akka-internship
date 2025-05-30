package com.project.json

import com.project.enums.ProjectStatus._
import com.project.enums.JobStatus._
import com.project.enums._
import com.project.model._
import org.joda.time.DateTime
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat}

import java.util.UUID

trait JsonFormats extends DefaultJsonProtocol {
  implicit object UUIDFormat extends JsonFormat[UUID] {
    override def write(uuid: UUID): JsValue = JsString(uuid.toString)
    override def read(value: JsValue): UUID = UUID.fromString(value.convertTo[String])
  }

  implicit object DateTimeFormat extends JsonFormat[DateTime] {
    override def write(dt: DateTime): JsValue = JsString(dt.toString)
    override def read(value: JsValue): DateTime = DateTime.parse(value.convertTo[String])
  }

  implicit object JobStatusFormat extends JsonFormat[JobStatus] {
    override def write(js: JobStatus): JsValue = JsString(js.toString)
    override def read(value: JsValue): JobStatus =
      value match {
        case JsString("Pending") => Pending
        case JsString("InProgress") => InProgress
        case JsString("Done") => Done
      }
  }

  implicit object ProjectStatusFormat extends JsonFormat[ProjectStatus] {
    override def write(ps: ProjectStatus): JsValue = JsString(ps.toString)
    override def read(value: JsValue): ProjectStatus =
      value match {
        case JsString("Active") => Active
        case JsString("Completed") => Completed
        case JsString("Deleted") => Deleted
      }
  }

  implicit val workspaceFormat: RootJsonFormat[Workspace] = jsonFormat5(Workspace)
  implicit val projectFormat: RootJsonFormat[Project] = jsonFormat7(Project)
  implicit val jobFormat: RootJsonFormat[Job] = jsonFormat8(Job)
}
