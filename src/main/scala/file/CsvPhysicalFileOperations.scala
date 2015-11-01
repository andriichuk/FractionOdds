package file

import java.io.{File, FileWriter, PrintWriter}
import resource._
import scala.io.Source
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CsvPhysicalFileOperations extends CsvFileOperations {

  def readByIndex(i: Int)(implicit fileName: String): Future[Option[Int]] =
    Future {
      managed(Source.fromURL(getFileUrl(fileName))) map {
        source =>
          val line = source.getLines().next()
          line.split(',')(i).toInt
      } opt
    }


  def writeByIndex(i: Int, value: Int)(implicit fileName: String): Future[Unit] =
    Future {

      var fileContent = ""

      // 1. Read file
      for (file <- managed(Source.fromURL(getFileUrl(fileName))))
      {
        fileContent = file.getLines().next()
      }

      // 2. Modify in-memory
      val lineValues = fileContent.split(',')
      lineValues(i) = value.toString

      // 3. Write to file
      for (file <- managed(new PrintWriter(new File(getFileUrl(fileName).toURI))))
      {
        file.write(lineValues.mkString(","))
      }
    }

  private def getFileUrl(fileName: String) = getClass().getResource(s"/$fileName")
}
