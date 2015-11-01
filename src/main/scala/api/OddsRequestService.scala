package api

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import core.ResultReceivingActor.RequestResultMessage
import spray.http.{MediaTypes, HttpResponse}
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global
import spray.routing.Directives
import scala.concurrent.duration._

class OddsRequestService(val resultReceivingActor: ActorRef)(implicit executionContext: ExecutionContext) extends Directives {

  implicit val timeout = Timeout(2.seconds)

  val route =
    get {
      path(IntNumber) { v1 =>
        respondWithMediaType(MediaTypes.`text/xml`) {
          complete {
            (resultReceivingActor ? RequestResultMessage(v1)).mapTo[Option[Int]].map {
              case Some(result) => s"<xml>${result}</xml>"
              case _ => "<xml/>"
            }
          }
        }
      }
    }
}