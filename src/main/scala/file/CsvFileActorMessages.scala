package file

object CsvFileActorMessages {
  case class ReadIndexMessage(i: Int)
  case class WriteIndexMessage(i: Int, value: Int)
}




