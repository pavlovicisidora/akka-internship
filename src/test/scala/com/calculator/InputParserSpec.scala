package com.calculator

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.calculator.model.{Add, ShowHistory, Subtract}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import utils.InputParser

class InputParserSpec extends TestKit(ActorSystem("TestSystem"))
  with ImplicitSender
  with AnyWordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "InputParser" should {
    "parse valid add input" in {
      InputParser.parse("2 + 3") shouldBe Some(Add(2.0, 3.0))
    }

    "parse valid subtract input" in {
      InputParser.parse("2 - 3") shouldBe Some(Subtract(2.0, 3.0))
    }

    "return None for invalid number" in {
      InputParser.parse("a + 3") shouldBe None
    }

    "return None for unknown operator" in {
      InputParser.parse("5 ^ 2") shouldBe None
    }

    "parse show command" in {
      InputParser.parse("show") shouldBe Some(ShowHistory)
    }

    "parse exit command" in {
      InputParser.parse("exit") shouldBe Some("exit")
    }
  }
}
