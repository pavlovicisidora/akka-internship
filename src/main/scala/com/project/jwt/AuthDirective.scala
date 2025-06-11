package com.project.jwt

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Directive1
import com.typesafe.config.ConfigFactory
import pdi.jwt.{Jwt, JwtAlgorithm}
import spray.json._

import java.util.UUID
import scala.util.{Try, Success, Failure}

object AuthDirective {
  private val config = ConfigFactory.load()

  val secretKey: String = config.getString("jwt.secret-key")

  def authenticate: Directive1[UUID] =
    optionalHeaderValueByName("Authorization").flatMap {
      case Some(token) if token.startsWith("Bearer ") =>
        val jwtString = token.substring("Bearer ".length)

        Jwt.decode(jwtString, secretKey, Seq(JwtAlgorithm.HS256)) match {
          case Success(claim) =>
            Try(claim.content.parseJson) match {
              case Success(jsValue) =>
                jsValue match {
                  case jsonObject: JsObject =>
                    jsonObject.fields.get("userId") match {
                      case Some(JsString(id)) =>
                        Try(UUID.fromString(id)) match {
                          case Success(uuid) => provide(uuid)
                          case Failure(ex) =>
                            println(s"[AuthDirective] Invalid UUID format in token: ${ex.getMessage}")
                            reject
                        }
                      case Some(otherValue) =>
                        println(s"[AuthDirective] 'userId' field in token is not a string: $otherValue")
                        reject
                      case None =>
                        println(s"[AuthDirective] 'userId' field missing from token claim: ${claim.content}")
                        reject
                    }
                  case _ =>
                    println(s"[AuthDirective] Token claim content is not a JSON object: ${claim.content}")
                    reject
                }
              case Failure(ex) =>
                println(s"[AuthDirective] Failed to parse JWT claim content as JSON: ${ex.getMessage} (Content: ${claim.content})")
                reject
            }

          case Failure(ex) =>
            println(s"[AuthDirective] Token decoding or validation failed: ${ex.getMessage}")
            reject
        }
      case _ =>
        println(s"[AuthDirective] No valid Authorization header provided.")
        reject
    }
}
