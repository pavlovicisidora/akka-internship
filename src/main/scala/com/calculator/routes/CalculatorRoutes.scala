package com.calculator.routes

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import akka.stream.scaladsl.Source
import akka.stream.OverflowStrategy
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
import akka.stream.Materializer
import akka.stream.SystemMaterializer
import com.calculator.utils.JsonFormats
import com.calculator.model._

import scala.concurrent.duration.DurationInt


class CalculatorRoutes(calculator: ActorRef) (implicit val timeout: Timeout, implicit val system: ActorSystem) extends JsonFormats {
  implicit val materializer: Materializer = SystemMaterializer(system).materializer
  val routes: Route =
    path("add") {
      post {
        entity(as[Add]) {
          add =>
            complete((calculator ? add).mapTo[Result])
        }
      }
    } ~
      path("subtract") {
        post {
          entity(as[Subtract]) {
            sub =>
              complete((calculator ? sub).mapTo[Result])
          }
        }
      } ~
      path("multiply") {
        post {
          entity(as[Multiply]) {
            mul =>
              complete((calculator ? mul).mapTo[Result])
          }
        }
      } ~
      path("divide") {
        post {
          entity(as[Divide]) {
            div =>
              complete((calculator ? div).mapTo[Result])
          }
        }
      } ~
      path("history") {
        get {
          complete((calculator ? ShowHistory).mapTo[History])
        }
      } ~
      path("stream-history") {
        get {
          complete {
            val source: Source[ServerSentEvent, ActorRef] =
              Source.actorRef[String](
                  bufferSize = 64,
                  overflowStrategy = OverflowStrategy.dropHead
                )
                .map(ServerSentEvent(_))
                .keepAlive(10.seconds, () => ServerSentEvent.heartbeat)

            val (actorRef, stream) = source.preMaterialize()

            calculator ! SubscribeToHistory(actorRef)

            stream.watchTermination(){ (_, done) =>
              done.onComplete(_ => calculator ! UnsubscribeFromHistory(actorRef))(scala.concurrent.ExecutionContext.global)
            }
            stream
          }
        }
      }
}
