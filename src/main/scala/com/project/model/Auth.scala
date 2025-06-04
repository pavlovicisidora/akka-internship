package com.project.model

import java.util.UUID

case class LoginRequest(email: String, password: String)
case class LoginResponse(id: UUID, token: String)
