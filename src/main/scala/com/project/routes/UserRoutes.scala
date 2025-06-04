package com.project.routes

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives.{as, complete, entity, onSuccess, path, pathPrefix, post}
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import com.project.json.JsonFormats
import com.project.model.{LoginRequest, UserRequestCreate}
import com.project.protocol.AuthProtocol.{AuthFailure, LoginSuccess, LoginUser, RegisterSuccess, RegisterUser}

import scala.concurrent.ExecutionContext

class UserRoutes(authActor: ActorRef)(implicit timeout: Timeout, ec: ExecutionContext) extends JsonFormats {
  val routes: Route =
    pathPrefix("auth") {
    path("register") {
      post {
        entity(as[UserRequestCreate]) { request =>
          onSuccess(authActor ? RegisterUser(request)) {
            case RegisterSuccess(user) => complete(user)
            case AuthFailure(msg) => complete(StatusCodes.BadRequest -> msg)
          }
        }
      }
    } ~
      path("login") {
        post {
          entity(as[LoginRequest]) { request =>
            onSuccess(authActor ? LoginUser(request)) {
              case LoginSuccess(token) => complete(token)
              case AuthFailure(msg) => complete(StatusCodes.Unauthorized -> msg)
            }
          }
        }
      }
  }
}
