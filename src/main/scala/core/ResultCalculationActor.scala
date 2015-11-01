package core

import akka.actor._
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import file.CsvFileActorMessages.{WriteIndexMessage, ReadIndexMessage}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import core.ResultCalculationActor.CalculateResultMessage

object ResultCalculationActor {
  case class CalculateResultMessage(v2: Int, v3: Int, v4: Int)
}

class ResultCalculationActor(interimResultsFileActor: ActorRef, conversionFileActor: ActorRef) extends Actor {

  implicit val timeout = Timeout(2.seconds)

  def receive: Receive = {
    case CalculateResultMessage(v2, v3, v4) => {
      (conversionFileActor ? ReadIndexMessage(v3)).mapTo[Option[Int]].map {
        case Some(fv3) if (fv3 + v2) < 10 =>
          interimResultsFileActor ! WriteIndexMessage(v4, fv3 + v2 + 10)
          Some(0)
        case Some(fv3) =>
          interimResultsFileActor ! WriteIndexMessage(v4, fv3 + v2)
          Some(1)
        case _ => None
      } pipeTo sender
    }

  }
}
