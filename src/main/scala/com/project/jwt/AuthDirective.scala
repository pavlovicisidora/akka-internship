package com.project.jwt

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Directive1
import pdi.jwt.{Jwt, JwtAlgorithm}
import spray.json._

import java.util.UUID

object AuthDirective {

  val secretKey = "secret-key"

  def authenticate: Directive1[UUID] =
    optionalHeaderValueByName("Authorization").flatMap {
      case Some(token) if token.startsWith("Bearer ") =>
        val jwt = token.substring("Bearer ".length)
        Jwt.decode(jwt, secretKey, Seq(JwtAlgorithm.HS256)) match {
          case scala.util.Success(claim) =>
            val json = claim.content.parseJson.asJsObject
            json.fields.get("userId") match {
              case Some(JsString(id)) =>
                provide(UUID.fromString(id))
              case _ =>
                reject
            }
          case scala.util.Failure(ex) =>
            println(s"[AuthDirective] Token decoding failed: ${ex.getMessage}")
            reject
        }
      case other =>
        println(s"[AuthDirective] No valid Authorization header: $other")
        reject
    }
}
