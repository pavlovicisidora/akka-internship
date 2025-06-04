package com.project.service.scalikejdbc

import com.project.model.{User, UserRequestCreate}
import com.project.repository.scalikejdbc.UserRepository
import scalikejdbc.{AutoSession, DBSession, ReadOnlyAutoSession}
import com.github.t3hnar.bcrypt._

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class UserService()(implicit ec: ExecutionContext) {
  private val userRepository = new UserRepository()

  def create(uRequest: UserRequestCreate)(implicit session: DBSession = AutoSession): Future[User] = {
    val userWithHashedPassword = uRequest.copy(passwordHash = uRequest.passwordHash.bcrypt)
    userRepository.create(userWithHashedPassword.toDomain)
  }

  def getById(id: UUID)(implicit session: DBSession = ReadOnlyAutoSession): Future[Option[User]] =
    userRepository.getById(id)

  def getByEmail(email: String)(implicit session: DBSession = ReadOnlyAutoSession): Future[Option[User]] =
    userRepository.getByEmail(email)

  def getAll()(implicit session: DBSession = ReadOnlyAutoSession): Future[List[User]] =
    userRepository.getAll

}
