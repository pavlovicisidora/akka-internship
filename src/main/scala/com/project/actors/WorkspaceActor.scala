package com.project.actors

import akka.actor.Actor
import akka.pattern.pipe
import com.project.protocol.WorkspaceProtocol._
import com.project.service.WorkspaceService

import scala.concurrent.ExecutionContext

class WorkspaceActor(workspaceService: WorkspaceService)(implicit ec: ExecutionContext) extends Actor {

  override def receive: Receive = {
    case CreateWorkspace(request) => workspaceService.create(request).pipeTo(sender)
    case GetWorkspace(id) => workspaceService.getById(id).pipeTo(sender)
    case UpdateWorkspace(id, request) => workspaceService.update(id, request).pipeTo(sender)
    case DeleteWorkspace(id) => workspaceService.delete(id).pipeTo(sender)
    case GetAllWorkspaces => workspaceService.getAll.pipeTo(sender)
  }

}
