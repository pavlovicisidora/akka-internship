package com.project.protocol

import com.project.model.{LoginRequest, LoginResponse, User, UserRequestCreate}

object AuthProtocol {
  case class RegisterUser(request: UserRequestCreate)
  case class LoginUser(request: LoginRequest)

  case class RegisterSuccess(user: User)
  case class LoginSuccess(response: LoginResponse)
  case class AuthFailure(message: String)
}
