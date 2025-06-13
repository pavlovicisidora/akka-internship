package com.project.routes

import akka.actor.ActorRef
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.project.json.JsonFormats
import com.project.jwt.AuthDirective
import com.project.model.{Project, ProjectRequestCreate, ProjectRequestCreateRaw, ProjectRequestUpdate}
import com.project.protocol.ProjectProtocol._

import scala.concurrent.{ExecutionContext, Future}

class ProjectRoutes(projectActor: ActorRef)(implicit timeout: Timeout, ec: ExecutionContext) extends JsonFormats {

  val routes: Route =
    pathPrefix("project") {
      pathEndOrSingleSlash {
        AuthDirective.authenticate { userId =>
          post {
            entity(as[ProjectRequestCreateRaw]) {
              rawRequest =>
                val enriched = ProjectRequestCreate.fromRaw(rawRequest, userId)
                val created: Future[Option[Project]] = (projectActor ? CreateProject(enriched)).mapTo[Option[Project]]
                complete(StatusCodes.Created -> created)
            }
          }
        } ~
          get {
            complete {
              (projectActor ? GetAllProjects).mapTo[List[Project]]
            }
          }
      } ~
        pathPrefix(JavaUUID) {
          id =>
            pathEndOrSingleSlash {
              get {
                val result = (projectActor ? GetProject(id)).mapTo[Option[Project]]
                onSuccess(result) {
                  case Some(p) => complete(StatusCodes.OK -> p)
                  case None     => complete(StatusCodes.NotFound -> s"Project with id $id not found")
                }
              } ~
                put {
                  entity(as[ProjectRequestUpdate]) {
                    request =>
                      val updated = (projectActor ? UpdateProject(id, request)).mapTo[Option[Project]]
                      onSuccess(updated) {
                        case Some(p) => complete(StatusCodes.OK -> p)
                        case None     => complete(StatusCodes.NotFound -> s"Project with id $id not found")
                      }
                  }
                } ~
                delete {
                  complete {
                    (projectActor ? DeleteProject(id)).mapTo[Option[Project]]
                  }
                }
            }
        }
    }

}
