package api

import core.{CoreActors, ActorTestableCore}
import org.mockito.Mockito._
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.mockito.Matchers.{eq => eqTo}
import org.specs2.specification.BeforeExample
import spray.routing.HttpService
import spray.routing.Directives
import spray.testkit.Specs2RouteTest
import spray.http._
import spray.http.StatusCodes._

import scala.concurrent.Future
import scala.xml.{Node, NodeSeq}


class OddsCalculationServiceSpec extends Specification with ActorTestableCore with CoreActors with Specs2RouteTest
with Directives with Api with Mockito with HttpService with BeforeExample{

  def actorRefFactory = system

  override def before: Any = reset(csvFileOperations)

  sequential

  "The service" should {

    "return correct xml result" in {
      csvFileOperations.readByIndex(eqTo(2))(anyString) returns Future(Some(12))

      Post("/", <xml><v2>1</v2><v3>2</v3><v4>3</v4></xml>) ~> oddsCalculationService.route ~> check {
        mediaType === MediaTypes.`text/xml`
        responseAs[String] === "<xml>1</xml>"
      }
    }

    "return BadRequest in case of malformed input" in {
      Post("/", <xml><v2>1</v2>no other params provided</xml>) ~> oddsCalculationService.route ~> check {
        status === StatusCodes.BadRequest
      }
    }

    "return ServerError in case of a file read exception" in {
      csvFileOperations.readByIndex(eqTo(2))(anyString) returns Future.failed(new UnsupportedOperationException)

      Post("/", <xml><v2>1</v2><v3>2</v3><v4>3</v4></xml>) ~> oddsCalculationService.route ~> check {
        status === StatusCodes.InternalServerError
      }
    }
  }

}
