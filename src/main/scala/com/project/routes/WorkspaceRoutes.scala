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
import com.project.model._
import com.project.protocol.WorkspaceProtocol._

import scala.concurrent.{ExecutionContext, Future}

class WorkspaceRoutes(workspaceActor: ActorRef)(implicit timeout: Timeout, ec: ExecutionContext) extends JsonFormats {
  
  val routes: Route =
    pathPrefix("workspace") {
      pathEndOrSingleSlash {
        post {
          AuthDirective.authenticate { userId =>
            entity(as[WorkspaceRequestCreateRaw]) {
              rawRequest =>
                val enriched = WorkspaceRequestCreate.fromRaw(rawRequest, userId)
                val created: Future[Workspace] = (workspaceActor ? CreateWorkspace(enriched)).mapTo[Workspace]
                complete(StatusCodes.Created -> created)
            }
          }
        } ~
          get {
            complete {
              (workspaceActor ? GetAllWorkspaces).mapTo[List[Workspace]]
            }
          }
      } ~
          pathPrefix(JavaUUID) {
            id =>
              pathEndOrSingleSlash {
                get {
                  val result = (workspaceActor ? GetWorkspace(id)).mapTo[Option[Workspace]]
                  onSuccess(result) {
                    case Some(ws) => complete(StatusCodes.OK -> ws)
                    case None     => complete(StatusCodes.NotFound -> s"Workspace with id $id not found")
                  }
                } ~
                  put {
                    entity(as[WorkspaceRequestUpdate]) {
                      request =>
                        val updated = (workspaceActor ? UpdateWorkspace(id, request)).mapTo[Option[Workspace]]
                        onSuccess(updated) {
                          case Some(ws) => complete(StatusCodes.OK -> ws)
                          case None     => complete(StatusCodes.NotFound -> s"Workspace with id $id not found")
                        }
                    }
                  } ~
                  delete {
                    complete {
                      (workspaceActor ? DeleteWorkspace(id)).mapTo[Boolean].map {
                        case true => StatusCodes.OK -> s"Workspace with id $id successfully deleted"
                        case false => StatusCodes.NotFound -> s"Workspace with id $id not found"
                      }
                    }
                  }
              }
          }
      }

}
