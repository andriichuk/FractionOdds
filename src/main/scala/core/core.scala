package core

import akka.actor._
import file._

/**
 * Core is type containing the ``system: ActorSystem`` member. This enables us to use it in our
 * apps as well as in our tests. Also it contains csvFileOperations, which
 * perform operations on real files or can be mocked while testing
 */
trait Core {

  implicit def system: ActorSystem

  def csvFileOperations: CsvFileOperations
}

/**
 * This trait implements ``Core`` by starting the required ``ActorSystem`` and registering the
 * termination handler to stop the system when the JVM exits.
 */
trait BootedCore extends Core {

  /**
   * Construct the ActorSystem we will use in our application
   */
  implicit lazy val system = ActorSystem("akka-spray")

  /**
   * Create operations on physical file
   */
  lazy val csvFileOperations = new CsvPhysicalFileOperations()

  /**
   * Ensure that the constructed ActorSystem is shut down when the JVM shuts down
   */
  sys.addShutdownHook(system.shutdown())

}

/**
 * This trait contains the actors that make up our application; it can be mixed in with
 * ``BootedCore`` for running code or ``TestKit`` for unit and integration tests.
 */
trait CoreActors {
  this: Core =>

  private lazy val _interimResultsFileActor = system.actorOf(Props(new InterimResultsFileActor(csvFileOperations)))
  private lazy val _conversionFileActor = system.actorOf(Props(new ConversionFileActor(csvFileOperations)))

  lazy val resultReceivingActor = system.actorOf(Props(new ResultReceivingActor(_interimResultsFileActor)))
  lazy val resultCalculationActor = system.actorOf(Props(new ResultCalculationActor(_interimResultsFileActor, _conversionFileActor)))

}