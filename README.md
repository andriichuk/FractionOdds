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
 
As the service is going to operate in a concurrent environment, the files represent shared resource that requires mutual exclusion of access. Since we chose to use Spray backed by actors, it would be wise to rely on actor in providing such concurrency. Since actors don't share their mutable state and because they receive only one message at a time, actors never need to attempt lock their state before reacting to a message. Therefore [ConversionFileActor](src/main/scala/file/ConversionFileActor.scala) and [InterimResultsActor](src/main/scala/file/InterimResultsActor.scala) were used for providing concurrent access for f1 and f2 files.

The service also uses a set of other actors, each of which incapsulates a specific behaviour. E.g. [ResultCalculationActor](src/main/scala/core/ResultCalculationActor.scala) performs calculation of coefficients and [ResultReceivingActor](src/main/scala/core/ResultReceivingActor.scala) provides interim results on request.

On top of the actors the service layer is built, represented by [OddsCalculationService](src/main/scala/api/OddsCalculationService.scala) and [OddsRequestService](src/main/scala/api/OddsRequestService.scala) and merged into a single REST service in [Api](src/main/scala/api/Api.scala) trait.

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
* **GET** http://127.0.0.1:8080/4. **Result**: *&lt;xml&gt;45&lt;/xml&gt;*
* **POST** http://127.0.0.1:8080/ *&lt;xml&gt;&lt;v2&gt;2&lt;/v2&gt;&lt;v3&gt;3&lt;/v3&gt;&lt;v4&gt;4&lt;/v4&gt;&lt;/xml&gt;*. **Result**: *&lt;xml&gt;0&lt;/xml&gt;*
* **GET** http://127.0.0.1:8080/4. **Result**: *&lt;xml&gt;6&lt;/xml&gt;*