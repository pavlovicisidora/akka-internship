package com.calculator

import com.calculator.actors.{AddActor, CalculatorActor, DivideActor, MultiplyActor, SubtractActor}
import com.calculator.model.{Add, Divide, Multiply, Result, ShowHistory, Subtract}
import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.duration._
import akka.util.Timeout

class CalculatorActorSpec extends TestKit(ActorSystem("TestSystem"))
  with ImplicitSender
  with AnyWordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  implicit val timeout: Timeout = Timeout(3.seconds)

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "CalculatorActor" should {
    "delegate operations and save to history" in {
      val addActor = system.actorOf(Props(new AddActor))
      val subtractActor = system.actorOf(Props(new SubtractActor))
      val multiplyActor = system.actorOf(Props(new MultiplyActor))
      val divideActor = system.actorOf(Props(new DivideActor))
      val historyProbe = TestProbe()

      val calculatorActor = system.actorOf(Props(new CalculatorActor(addActor, subtractActor, multiplyActor, divideActor, historyProbe.ref)))
      calculatorActor ! Add(2.0, 3.0)
      val expectedResultAdd = Result("2.0 + 3.0 = 5.0")
      historyProbe.expectMsg(expectedResultAdd.output)
      expectMsg(expectedResultAdd)

      calculatorActor ! Subtract(2.0, 3.0)
      val expectedResultSub = Result("2.0 - 3.0 = -1.0")
      historyProbe.expectMsg(expectedResultSub.output)
      expectMsg(expectedResultSub)

      calculatorActor ! Multiply(2.0, 3.0)
      val expectedResultMul = Result("2.0 * 3.0 = 6.0")
      historyProbe.expectMsg(expectedResultMul.output)
      expectMsg(expectedResultMul)

      calculatorActor ! Divide(6.0, 3.0)
      val expectedResultDiv = Result("6.0 / 3.0 = 2.0")
      historyProbe.expectMsg(expectedResultDiv.output)
      expectMsg(expectedResultDiv)
    }

    "handle ShowHistory command" in {
      val probe = TestProbe()
      val historyProbe = TestProbe()
      val calculator = system.actorOf(Props(new CalculatorActor(probe.ref, probe.ref, probe.ref, probe.ref, historyProbe.ref)))
      calculator ! ShowHistory
      historyProbe.expectMsg("show")
    }

    "handle dividing by zero" in {
      val probe = TestProbe()
      val divideActor = system.actorOf(Props(new DivideActor))
      val historyProbe = TestProbe()

      val calculator = system.actorOf(Props(new CalculatorActor(probe.ref, probe.ref, probe.ref, divideActor, historyProbe.ref)))
      calculator ! Divide(6.0, 0.0)
      val expectedResultDivBy0 = Result("You cannot divide by zero")
      historyProbe.expectMsg(expectedResultDivBy0.output)
      expectMsg(expectedResultDivBy0)
    }
  }

}
