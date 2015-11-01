package core

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import core.ResultCalculationActor._
import core.ResultReceivingActor._
import file.CsvFileOperations
import org.mockito.Mockito._
import org.specs2.mutable._
import org.specs2.Specification
import org.specs2.mock.Mockito
import org.mockito.Matchers._
import org.mockito.Matchers.{eq => eqTo}
import org.specs2.specification.BeforeExample
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class ResultCalculationActorSpec extends TestKit(ActorSystem())
with SpecificationLike with ActorTestableCore with CoreActors with ImplicitSender
with Mockito with BeforeExample {

  override def before: Any = reset(csvFileOperations)

  sequential

  "The actor should" >> {

    "return 0 if condition succeeds" in {

      csvFileOperations.readByIndex(eqTo(2))(anyString) returns Future(Some(4))
      resultCalculationActor ! CalculateResultMessage(1, 2, 3)
      expectMsg(Some(0))  // 4 + 1 < 10 ? true => 0

      success
    }

    "return 1 if condition fails" in {

      csvFileOperations.readByIndex(eqTo(2))(anyString) returns Future(Some(12))
      resultCalculationActor ! CalculateResultMessage(1, 2, 3)
      expectMsg(Some(1))  // 12 + 1 < 10 ? false => 1

      success
    }

    "save correct result for succeeding condition" in {

      csvFileOperations.readByIndex(eqTo(2))(anyString) returns Future(Some(4))
      resultCalculationActor ! CalculateResultMessage(1, 2, 3)
      there was one(csvFileOperations).writeByIndex(eqTo(3), eqTo(15))(anyString)  // f2[3] = 4 + 1 + 10

      success
    }

    "save correct result for failed condition" in {

      csvFileOperations.readByIndex(eqTo(2))(anyString) returns Future(Some(12))
      resultCalculationActor ! CalculateResultMessage(1, 2, 3)
      there was one(csvFileOperations).writeByIndex(eqTo(3), eqTo(13))(anyString)  // f2[3] = 12 + 1

      success
    }
  }
}
