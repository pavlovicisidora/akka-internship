package com.project

import com.project.model.Workspace
import com.project.repository.scalikejdbc.WorkspaceRepository
import scalikejdbc.{DB, scalikejdbcSQLInterpolationImplicitDef}

import java.util.UUID

class WorkspaceRepositorySpec extends DatabaseSpec {

  val repository = new WorkspaceRepository()
  val userId: UUID = UUID.fromString("33333333-3333-3333-3333-333333333333")

  private def insertTestUser(): Unit = {
    DB localTx { implicit session =>
      sql"""
        INSERT INTO users (id, email, passwordHash)
        VALUES ($userId, 'test@test.com', 'hashed')
      """.update.apply()
    }
  }

  private def newWorkspace(name: String): Workspace = Workspace(
    id = UUID.randomUUID(),
    name = name,
    description = Some("description"),
    createdBy = userId
  )

  "WorkspaceRepository" should {
    "create and fetch workspace by ID" in {
      insertTestUser()
      val workspace = newWorkspace("Test Workspace")

      for {
        created <- repository.create(workspace)
        found <- repository.getById(created.id)
      } yield {
        found shouldBe defined
        found.get.name shouldBe "Test Workspace"
        found.get.description shouldBe Some("description")
        found.get.createdBy shouldBe userId
      }
    }

    "return None for non-existing workspace" in {
      repository.getById(UUID.randomUUID()).map(_ shouldBe None)
    }

    "fetch all workspaces" in {
      insertTestUser()
      val ws1 = newWorkspace("WS 1")
      val ws2 = newWorkspace("WS 2")
      for {
        _ <- repository.create(ws1)
        _ <- repository.create(ws2)
        all <- repository.getAll()
      } yield {
        all.map(_.name).toSet should contain allElementsOf Set("WS 1", "WS 2")
      }
    }

    "update workspace successfully" in {
      insertTestUser()
      val original = newWorkspace("Original Name")
      for {
        created <- repository.create(original)
        updated <- repository.update(created.copy(name = "Updated Name", description = Some("Updated desc")))
        fetched <- repository.getById(created.id)
      } yield {
        updated shouldBe defined
        fetched shouldBe defined
        fetched.get.name shouldBe "Updated Name"
        fetched.get.description shouldBe Some("Updated desc")
      }
    }

    "delete workspace by ID" in {
      insertTestUser()
      val ws = newWorkspace("To Delete")
      for {
        created <- repository.create(ws)
        deleted <- repository.delete(created.id)
        afterDelete <- repository.getById(created.id)
      } yield {
        deleted shouldBe true
        afterDelete shouldBe None
      }
    }
  }
}
