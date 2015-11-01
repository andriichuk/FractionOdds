package file

import akka.actor._
import akka.pattern.pipe
import file.CsvFileActorMessages._
import scala.concurrent.ExecutionContext.Implicits.global

class InterimResultsFileActor(csvFileOperations: CsvFileOperations) extends Actor {
  implicit private val fileName = "f2.csv"

  def receive: Receive = {
    case ReadIndexMessage(i) => csvFileOperations.readByIndex(i) pipeTo sender
    case WriteIndexMessage(i, value) => csvFileOperations.writeByIndex(i, value)
  }
}
