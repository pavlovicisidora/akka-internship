package com.project

import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import scalikejdbc.config.DBs
import scalikejdbc.{DB, scalikejdbcSQLInterpolationImplicitDef}

trait DatabaseSpec extends AsyncWordSpec with Matchers with BeforeAndAfterEach {
  override def beforeEach(): Unit = {
    DBs.setupAll()
    cleanDatabase()
  }

  override def afterEach(): Unit = {
    DBs.closeAll()
  }

  private def cleanDatabase(): Unit = {
    DB localTx { implicit session =>
      sql"DELETE FROM jobs".update.apply()
      sql"DELETE FROM projects".update.apply()
      sql"DELETE FROM workspaces".update.apply()
      sql"DELETE FROM users".update.apply()
    }
  }
}
