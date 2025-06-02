package com.project.actors

import akka.actor.{Actor, ActorRef}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import akka.util.Timeout
import com.project.Main.system
import com.project.routes._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

class HttpServerActor(
                       workspaceActor: ActorRef,
                       projectActor: ActorRef,
                       jobActor: ActorRef)(implicit mat: Materializer, ec: ExecutionContext) extends Actor{

  implicit val timeout: Timeout = Timeout(3.seconds)
  private val workspaceRoutes = new WorkspaceRoutes(workspaceActor)
  private val projectRoutes = new ProjectRoutes(projectActor)
  private val jobRoutes = new JobRoutes(jobActor)

  val routes: Route = workspaceRoutes.routes ~ projectRoutes.routes ~ jobRoutes.routes

  Http().newServerAt("localhost", 8080).bind(routes)

  override def receive: Receive = Actor.emptyBehavior
}
