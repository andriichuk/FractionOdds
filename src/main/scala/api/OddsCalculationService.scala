package api

import akka.util.Timeout
import core.ResultCalculationActor.CalculateResultMessage
import core.ResultReceivingActor.RequestResultMessage
import spray.http.{HttpResponse, StatusCodes, StatusCode, MediaTypes}
import spray.routing.Directives
import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.ActorRef
import akka.pattern.ask
import scala.concurrent.duration._
import scala.util.{Success, Try}
import scala.xml.NodeSeq

class OddsCalculationService(val resultCalculationActor: ActorRef)(implicit executionContext: ExecutionContext) extends Directives {

  implicit val timeout = Timeout(2.seconds)

  val route =
    post {
      entity(as[NodeSeq]) { xml =>
          respondWithMediaType(MediaTypes.`text/xml`) {
            complete {
              val response = parseInput(xml).map {
                case (v2, v3, v4) =>
                  (resultCalculationActor ? CalculateResultMessage(v2, v3, v4)).mapTo[Option[Int]].map {
                    case Some(result) => StatusCodes.OK -> s"<xml>${result}</xml>"
                    } fallbackTo { Future { StatusCodes.InternalServerError -> "" } }
              } getOrElse { Future { StatusCodes.BadRequest -> "" }}
              response
            }
          }
        }
    }

  private def parseInput(xml: NodeSeq) = Try { ((xml \ "v2").text.toInt, (xml \ "v3").text.toInt, (xml \ "v4").text.toInt) }
}