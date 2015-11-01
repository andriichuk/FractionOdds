package file

import scala.concurrent.Future

trait CsvFileOperations {

  def readByIndex(i: Int)(implicit fileName: String): Future[Option[Int]]

  def writeByIndex(i: Int, value: Int)(implicit fileName: String): Future[Unit]

}
