package com.project

import akka.actor.{ActorSystem, Props}
import com.project.actors.{AuthActor, HttpServerActor, JobActor, ProjectActor, WorkspaceActor}
import com.project.service.scalikejdbc.UserService
//import com.project.repository.slick.{JobRepository, ProjectRepository, WorkspaceRepository}
//import com.project.service.slick.{JobService, ProjectService, WorkspaceService}
import com.project.service.scalikejdbc.{JobService, ProjectService, WorkspaceService}
import scalikejdbc.config._

import scala.concurrent.ExecutionContextExecutor
//import slick.jdbc.PostgresProfile.api._

object Main extends App {
  implicit val system: ActorSystem = ActorSystem("ProjectSystem")
  implicit  val ec: ExecutionContextExecutor = system.dispatcher
  /*val db = Database.forConfig("db")
  val workspaceRepositorySlick = new WorkspaceRepository(db)
  val projectRepositorySlick = new ProjectRepository(db)
  val jobRepositorySlick = new JobRepository(db)*/

  /*val workspaceService = new WorkspaceService(workspaceRepository)
  val projectService = new ProjectService(projectRepository, workspaceRepository)
  val jobService = new JobService(jobRepository, projectRepository)*/

  DBs.setupAll()
  val workspaceService = new WorkspaceService()
  val projectService = new ProjectService()
  val jobService = new JobService()
  val userService = new UserService()

  val workspaceActor = system.actorOf(Props(new WorkspaceActor(workspaceService)), "workspace-actor")
  val projectActor = system.actorOf(Props(new ProjectActor(projectService)), "project-actor")
  val jobActor = system.actorOf(Props(new JobActor(jobService)), "job-actor")
  val authActor = system.actorOf(Props(new AuthActor(userService, "secret-key")), "auth-actor")

  system.actorOf(Props(new HttpServerActor(workspaceActor, projectActor, jobActor, authActor)))
}
