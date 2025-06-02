package com.project.routes

import akka.actor.ActorRef
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.project.json.JsonFormats
import com.project.model.{Job, JobRequestCreate, JobRequestUpdate}
import com.project.protocol.JobProtocol._

import scala.concurrent.{ExecutionContext, Future}

class JobRoutes(jobActor: ActorRef)(implicit timeout: Timeout, ec: ExecutionContext) extends JsonFormats {

  val routes: Route =
    pathPrefix("job") {
      pathEndOrSingleSlash {
        post {
          entity(as[JobRequestCreate]) {
            request =>
              val created: Future[Option[Job]] = (jobActor ? CreateJob(request)).mapTo[Option[Job]]
              complete(StatusCodes.Created -> created)
          }
        } ~
          get {
            complete {
              (jobActor ? GetAllJobs).mapTo[List[Job]]
            }
          }
      } ~
        pathPrefix(JavaUUID) {
          id =>
            pathEndOrSingleSlash {
              get {
                val result = (jobActor ? GetJob(id)).mapTo[Option[Job]]
                onSuccess(result) {
                  case Some(j) => complete(StatusCodes.OK -> j)
                  case None     => complete(StatusCodes.NotFound -> s"Job with id $id not found")
                }
              } ~
                put {
                  entity(as[JobRequestUpdate]) {
                    request =>
                      val updated = (jobActor ? UpdateJob(id, request)).mapTo[Option[Job]]
                      onSuccess(updated) {
                        case Some(j) => complete(StatusCodes.OK -> j)
                        case None     => complete(StatusCodes.NotFound -> s"Job with id $id not found")
                      }
                  }
                } ~
                delete {
                  complete {
                    (jobActor ? DeleteJob(id)).mapTo[Boolean].map {
                      case true => StatusCodes.OK -> s"Job with id $id successfully deleted"
                      case false => StatusCodes.NotFound -> s"Job with id $id not found"
                    }
                  }
                }
            }
        }
    }

}
