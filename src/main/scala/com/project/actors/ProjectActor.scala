package com.project.actors

import akka.actor.Actor
import akka.pattern.pipe
import com.project.protocol.JobProtocol.GetJob
import com.project.protocol.ProjectProtocol._
//import com.project.service.slick.ProjectService
import com.project.service.scalikejdbc.ProjectService

import scala.concurrent.ExecutionContext

class ProjectActor(projectService: ProjectService)(implicit ec: ExecutionContext) extends Actor {

  override def receive: Receive = {
    case CreateProject(request) => projectService.create(request).pipeTo(sender)
    case GetProject(id) => projectService.getById(id).pipeTo(sender)
    case UpdateProject(id, request) => projectService.update(id, request).pipeTo(sender)
    case DeleteProject(id) => projectService.delete(id).pipeTo(sender)
    case GetAllProjects => projectService.getAll.pipeTo(sender)
  }

}
