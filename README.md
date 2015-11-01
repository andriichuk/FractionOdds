# Fraction odds calculation REST service

High-loaded multi-user REST service performing calculation of 'special' coefficients (fraction odds).

## Toolkit
[Spray](http://spray.io) toolit was chosen for implementation due to the following reasons: 
1. Lightweight.
2. Provides intuitive DSL for defining routes.
3. Actor-based, which in turn, provide a concrete approach to building [reactive](http://www.reactivemanifesto.org) systems. That was a large advantage over [Scalatra](http://www.scalatra.org).

## Tech
The service uses two files for processing requests:
* file of input conversion (f1)
* file of interim results (f2)
 
As the service is going to operate in a concurrent environment, the files represent shared resource that requires mutual exclusion of access. Since we chose to use Spray backed by actors, it would be wise to rely on actor in providing such concurrency. Since actors don't share their mutable state and because they receive only one message at a time, actors never need to attempt lock their state before reacting to a message. Therefore [ConversionFileActor](ConversionFileActor.scala) and [InterimResultsActor](InterimResultsActor.scala) were used for providing concurrent access for f1 and f2 files.

The service also uses a set of other actors, each of which incapsulates a specific behaviour. E.g. [ResultCalculationActor](src/main/scala/core/ResultCalculationActor.scala) performs calculation of coefficients and [ResultReceivingActor](src/main/scala/core/ResultReceiving.scala) provides interim results on request.

On top of the actors the service layer is built, represented by [OddsCalculationService](src/main/scala/api/OddsCalculationService) and [OddsRequestService](src/main/scala/api/OddsRequestService) and merged into a single REST service in [Api](src/main/scala/api/Api.scala) trait.

## Evidence

The solution is covered by tests, that can be run by:
```sh
sbt test
```

Also it's certainly possible to launch a web server:
```sh
sbt run
```
After that you'll be able to get/post requests to the running server. E.g. for default [f1.csv](src/main/resources/f1.csv) and [f2.csv](src/main/resources/f2.csv) files, the following sequence should hold true:
* **GET** http://127.0.0.1:8080/4. **Result**: *<xml>45</xml>*
* **POST** http://127.0.0.1:8080/ *<xml><v2>2</v2><v3>3</v3><v4>4</v4></xml>*. **Result**: *<xml>0</xml>*
* **GET** http://127.0.0.1:8080/4. **Result**: *<xml>6</xml>*