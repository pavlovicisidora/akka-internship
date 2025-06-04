package com.project.actors

import akka.actor.Actor
import akka.pattern.pipe
import com.project.model.LoginResponse
import com.project.protocol.AuthProtocol.{AuthFailure, LoginSuccess, LoginUser, RegisterSuccess, RegisterUser}
import com.project.service.scalikejdbc.UserService
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}

import com.github.t3hnar.bcrypt._
import java.time.Clock
import scala.concurrent.ExecutionContext

class AuthActor (userService: UserService, secretKey: String)(implicit ec: ExecutionContext) extends Actor {

  override def receive: Receive = {
    case RegisterUser(request) =>
      userService.create(request).map(RegisterSuccess).recover {
        case ex => AuthFailure(ex.getMessage)
      }.pipeTo(sender())

    case LoginUser(request) =>
      userService.getByEmail(request.email).map {
        case Some(user) if request.password.isBcrypted(user.passwordHash) =>
          val claim = JwtClaim(
            content = s"""{"userId":"${user.id}"}""",
            expiration = Some(Clock.systemUTC().instant().getEpochSecond + 3600)
          )
          val token = Jwt.encode(claim, secretKey, JwtAlgorithm.HS256)
          LoginSuccess(LoginResponse(user.id, token))

        case Some(_) =>
          AuthFailure("Invalid password")

        case None =>
          AuthFailure("User not found")
      }.pipeTo(sender())
  }

}
