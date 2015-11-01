package core

import file.CsvFileOperations
import org.specs2.mock.Mockito

trait ActorTestableCore extends Core with Mockito {

  lazy val csvFileOperations: CsvFileOperations = smartMock[CsvFileOperations]

}
