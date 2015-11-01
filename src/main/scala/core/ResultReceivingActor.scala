package core

import akka.actor._
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import core.ResultReceivingActor._
import file.CsvFileActorMessages.ReadIndexMessage
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object ResultReceivingActor {
  case class RequestResultMessage(v1: Int)
}

class ResultReceivingActor(interimResultsFileActor: ActorRef) extends Actor {

  implicit val timeout = Timeout(2.seconds)

  def receive: Receive = {
    case RequestResultMessage(v1) => {
      (interimResultsFileActor ? ReadIndexMessage(v1)).mapTo[Option[Int]].map {
        case Some(fv1) if fv1 > 10 => Some(fv1 - 10)
        case Some(fv1) => Some(fv1)
        case _ => None
      } pipeTo sender
    }
  }
}
