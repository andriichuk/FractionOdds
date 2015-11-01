package core

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import core.ResultReceivingActor._
import file.CsvFileOperations
import org.specs2.mutable.SpecificationLike
import org.specs2.Specification
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.specs2.mock.{Mockito, mockito}
import org.mockito.Matchers.{eq => eqTo}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class ResultReceivingActorSpec extends TestKit(ActorSystem())
  with SpecificationLike with ActorTestableCore with CoreActors with ImplicitSender
  with Mockito {

  sequential

  "The actor" >> {

    "correct results when needed" in {

      csvFileOperations.readByIndex(eqTo(0))(anyString) returns Future(Some(11))
      resultReceivingActor ! RequestResultMessage(0)
      expectMsg(Some(1))  // 11 > 10 ? true => 11 - 10

      csvFileOperations.readByIndex(eqTo(0))(anyString) returns Future(Some(20))
      resultReceivingActor ! RequestResultMessage(0)
      expectMsg(Some(10)) // 20 > 10 ? true => 20 - 10

      csvFileOperations.readByIndex(eqTo(0))(anyString) returns Future(Some(8))
      resultReceivingActor ! RequestResultMessage(0)
      expectMsg(Some(8))  // 8 > 10 ? false => 8

      success
    }

    "return None if wrong index was provided" in {

      csvFileOperations.readByIndex(eqTo(0))(anyString) returns Future(None)
      resultReceivingActor ! RequestResultMessage(0)
      expectMsg(None)

      success
    }
  }
}
