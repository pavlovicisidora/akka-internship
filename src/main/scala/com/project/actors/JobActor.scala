package com.project.actors

import akka.actor.Actor
import akka.pattern.pipe
import com.project.protocol.JobProtocol._
import com.project.service.JobService

import scala.concurrent.ExecutionContext

class JobActor(jobService: JobService)(implicit ec: ExecutionContext) extends Actor {

  override def receive: Receive = {
    case CreateJob(request) => jobService.create(request).pipeTo(sender)
    case GetJob(id) => jobService.getById(id).pipeTo(sender)
    case UpdateJob(id, request) => jobService.update(id, request).pipeTo(sender)
    case DeleteJob(id) => jobService.delete(id).pipeTo(sender)
    case GetAllJobs => jobService.getAll.pipeTo(sender)
  }

}
