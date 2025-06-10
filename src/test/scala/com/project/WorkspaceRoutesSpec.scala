package com.project

import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.server.Directives.provide
import akka.http.scaladsl.server.{Directive1, Route}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.testkit.TestProbe
import akka.util.Timeout
import com.project.model.{TriState, Workspace, WorkspaceRequestCreate, WorkspaceRequestCreateRaw, WorkspaceRequestUpdate}
import com.project.routes.WorkspaceRoutes
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import spray.json.enrichAny
import com.project.json.JsonFormats
import com.project.protocol.WorkspaceProtocol._
import org.joda.time.DateTime
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}

import java.util.UUID
import scala.concurrent.duration.DurationInt

object TestAuthDirective {
  def authenticate: Directive1[UUID] = provide(UUID.fromString("33333333-3333-3333-3333-333333333333"))
}

class WorkspaceRoutesSpec extends AnyWordSpec with Matchers with ScalatestRouteTest with JsonFormats {
  implicit val timeout: Timeout = Timeout(3.seconds)
  "WorkspaceRoutes" should {
    val probe = TestProbe()
    val routes: Route = new WorkspaceRoutes(probe.ref).routes

    "create workspace" in {
      val name = "Workspace"
      val description = "Workspace's description"
      val rawRequest = WorkspaceRequestCreateRaw(name, Some(description))
      val userId = UUID.fromString("33333333-3333-3333-3333-333333333333")
      val secretKey = "secret-key"
      val claim = JwtClaim(
        content = s"""{"userId":"$userId"}""",
        expiration = Some((System.currentTimeMillis() / 1000) + 3600)
      )
      val token = Jwt.encode(claim, secretKey, JwtAlgorithm.HS256)

      val httpRequest = Post("/workspace")
        .withEntity(ContentTypes.`application/json`, rawRequest.toJson.prettyPrint)
        .withHeaders(Authorization(OAuth2BearerToken(token)))

      val request = WorkspaceRequestCreate(rawRequest.name, rawRequest.description, userId)
      val result = httpRequest ~> routes
      val receivedMessage = probe.expectMsgType[CreateWorkspace]
      receivedMessage.request shouldEqual request

      val createdWorkspace = Workspace(
        id = UUID.randomUUID(),
        name = name,
        description = Some(description),
        created_by = UUID.randomUUID()
      )
      probe.reply(createdWorkspace)

      result ~> check {
        status shouldBe StatusCodes.Created
      }
    }

    "update workspace" in {
      val id = UUID.randomUUID()
      val json = """{"description": "new description"}"""
      val requestResult = Put(s"/workspace/$id")
        .withEntity(ContentTypes.`application/json`, json) ~> routes

      val msg = probe.expectMsgType[UpdateWorkspace]
      msg.id shouldEqual id
      msg.request shouldEqual WorkspaceRequestUpdate(None, Some(TriState.Set("new description")))

      val workspace = Some(Workspace(id = id, name = "Test", description = Some("new description"), created_by = UUID.randomUUID()))
      probe.reply(workspace)

      requestResult ~> check {
        status shouldEqual StatusCodes.OK
      }
    }

    "delete workspace" in {
      val id = UUID.randomUUID()
      val requestResult = Delete(s"/workspace/$id") ~> routes

      val msg = probe.expectMsgType[DeleteWorkspace]
      msg.id shouldEqual id
      probe.reply(true)

      requestResult ~> check {
        status shouldEqual StatusCodes.OK
      }
    }

    "get workspace" in {
      val id = UUID.randomUUID()
      val expectedWorkspace = Workspace(
        id = id,
        name = "Test",
        description = Some("Description test"),
        created_at = DateTime.now,
        updated_at = DateTime.now,
        created_by = UUID.randomUUID()
      )
      val requestResult = Get(s"/workspace/$id") ~> routes

      val msg = probe.expectMsgType[GetWorkspace]
      msg.id shouldEqual id
      probe.reply(Some(expectedWorkspace))

      requestResult ~> check {
        status shouldBe StatusCodes.OK
        contentType shouldBe ContentTypes.`application/json`
        val returned = entityAs[Workspace]
        returned.id shouldEqual expectedWorkspace.id
        returned.name shouldEqual expectedWorkspace.name
        returned.description shouldEqual expectedWorkspace.description
        returned.created_at.toString shouldEqual expectedWorkspace.created_at.toString
        returned.updated_at.toString shouldEqual expectedWorkspace.updated_at.toString
      }
    }

    "get all workspaces" in {
      val workspaces = List(
        Workspace(
          id = UUID.randomUUID(),
          name = "Workspace 1",
          description = Some("Description 1"),
          created_at = DateTime.now,
          updated_at = DateTime.now,
          created_by = UUID.randomUUID()
        ),
        Workspace(
          id = UUID.randomUUID(),
          name = "Workspace 2",
          description = Some("Description 2"),
          created_at = DateTime.now,
          updated_at = DateTime.now,
          created_by = UUID.randomUUID()
        )
      )

      val requestResult = Get("/workspace") ~> routes

      probe.expectMsgType[GetAllWorkspaces.type]
      probe.reply(workspaces)

      requestResult ~> check {
        status shouldBe StatusCodes.OK
        contentType shouldBe ContentTypes.`application/json`

        val returned = entityAs[List[Workspace]]
        returned.zip(workspaces).foreach { case (r, e) =>
          r.id shouldBe e.id
          r.name shouldBe e.name
          r.description shouldBe e.description
        }
      }
    }
  }
}
