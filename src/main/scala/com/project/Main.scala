package com.project

import akka.actor.{ActorSystem, Props}
import com.project.actors.{HttpServerActor, JobActor, ProjectActor, WorkspaceActor}
import com.project.repository.{JobRepository, ProjectRepository, WorkspaceRepository}
import com.project.service.{JobService, ProjectService, WorkspaceService}

import scala.concurrent.ExecutionContextExecutor

object Main extends App {
  implicit val system: ActorSystem = ActorSystem("ProjectSystem")
  implicit  val ec: ExecutionContextExecutor = system.dispatcher

  val workspaceRepository = new WorkspaceRepository()
  val projectRepository = new ProjectRepository()
  val jobRepository = new JobRepository()

  val workspaceService = new WorkspaceService(workspaceRepository)
  val projectService = new ProjectService(projectRepository, workspaceRepository)
  val jobService = new JobService(jobRepository, projectRepository)

  val workspaceActor = system.actorOf(Props(new WorkspaceActor(workspaceService)), "workspace-actor")
  val projectActor = system.actorOf(Props(new ProjectActor(projectService)), "project-actor")
  val jobActor = system.actorOf(Props(new JobActor(jobService)), "job-actor")

  system.actorOf(Props(new HttpServerActor(workspaceActor, projectActor, jobActor)))
}
