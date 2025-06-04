package com.project.model

import java.util.UUID

case class User(id: UUID, email: String, passwordHash: String)

case class UserRequestCreate(
                              email: String,
                              passwordHash: String
                            ) {

  def toDomain : User =
    User(UUID.randomUUID(), email, passwordHash)
}
