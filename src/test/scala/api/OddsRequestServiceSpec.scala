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

class OddsRequestServiceSpec extends Specification with ActorTestableCore with CoreActors with Specs2RouteTest
  with Directives with Api with Mockito with HttpService with BeforeExample{

  def actorRefFactory = system

  override def before: Any = reset(csvFileOperations)

  sequential

  "The service" should {

    "return correct xml result" in {
      csvFileOperations.readByIndex(eqTo(1))(anyString) returns Future(Some(5))

      Get("/1") ~> oddsRequestService.route ~> check {
        mediaType === MediaTypes.`text/xml`
        responseAs[String] === "<xml>5</xml>"
      }
    }

    "return empty xml if no result found" in {
      csvFileOperations.readByIndex(eqTo(1))(anyString) returns Future(None)

      Get("/1") ~> oddsRequestService.route ~> check {
        mediaType === MediaTypes.`text/xml`
        responseAs[String] === "<xml/>"
      }
    }
  }
}
