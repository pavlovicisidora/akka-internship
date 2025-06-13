package com.project.repository.scalikejdbc

import com.project.model.User
import scalikejdbc.{AutoSession, DBSession, ReadOnlyAutoSession, SQLSyntaxSupport, WrappedResultSet, scalikejdbcSQLInterpolationImplicitDef}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class UserRepository()(implicit ec: ExecutionContext) extends SQLSyntaxSupport[User] {

  def apply(rs: WrappedResultSet): User = User(
    id = UUID.fromString(rs.string("id")),
    email = rs.string("email"),
    passwordHash = rs.string("passwordHash")
  )

  def create(user: User)(implicit session: DBSession = AutoSession): Future[User] = Future {
    sql"""
         INSERT INTO users(id, email, passwordHash)
         VALUES (${user.id}, ${user.email}, ${user.passwordHash})
       """.update.apply()
    user
  }

  def getById(id: UUID)(implicit session: DBSession = ReadOnlyAutoSession): Future[Option[User]] = Future {
    sql"""SELECT * FROM users WHERE id = $id"""
      .map(rs => apply(rs)).single.apply()
  }

  def getByEmail(email: String)(implicit session: DBSession = ReadOnlyAutoSession): Future[Option[User]] = Future {
    sql"""SELECT * FROM users WHERE email= $email"""
      .map(rs => apply(rs)).single.apply()
  }

  def getAll()(implicit session: DBSession = ReadOnlyAutoSession): Future[List[User]] = Future {
    sql"SELECT * FROM users".map(apply).list.apply()
  }
}
