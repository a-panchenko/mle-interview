# mle-interview
MLE Interview Take Home Exercise 

## High Level Design

![architecture](https://i.ibb.co/370NyWS/mle-interview-architecture.png)

[Link in case if the image above is not working](https://ibb.co/R9C0pPQ)

## Components Overview

### Feature Calculator

A Python script that reads input data from the data storage,
which is a folder with csv files in this case, computes features according
to the specification, and saves the result as a JSONL file in the
feature storage, which is a folder in this case.

### HTTP API(order-status-recommender-api)

Scala + Akka HTTP application. Exposes one endpoint:

* POST /decision/v1 -- produces recommendation for the order. Uses the features storage and the provided model to produce predictions.

#### Architecture overview

The API follows "Ports and Adapters" pattern. `Core` package contains domain classes and `MLOrderStatusRecommendationService` which performs core business
logic. `Port` package contains interfaces to the outer world for our core. `Port.in` contains `OrderStatusRecommendationService` interface that can be used
to trigger running of our business logic. `Port.out` contains `FeatureStorage` and `OrderStatusRecommendationMLModel` interfaces.
These interfaces are used by our business logic to communicate with the outer world proactively. `Adapter.in` package includes `http` package which contains
classes for providing the outer world possibility to access the business logic. In similar way, we could have `adapter.in.kafka` package with classes
responsible for triggering business logic via streaming interface rather than HTTP. `Adapter.out` has classes that implement interfaces from `port.out` package,
those classes are not used from business logic directly. `Boot` package contains classes responsible for getting all components together and running the application.

The main idea of this approach is to isolate core business logic from outer world by using abstract interfaces(ports). Although in this specific case this
architecture may be an overkill, because we have only one simple endpoint, it can be quite easily extended to expose business logic via messaging systems or to
use more sophisticated storage systems.

### Batch Recommender

A Python script that reads `order_id` of all orders from the input data storage and requests order status recommendation from the HTTP API
for each order. The result is stored as JSONL file in the result storage, which is a simple folder. 

## How to run?

In `run.sh` script change value of `LOCAL_ROOT_FOLDER` variable to the root folder on your machine and run the script.

## What could be improved?

* Naming of the http api should be fixed. Currently, it's `order-status-recommender-api` folder, in `docker-compose.yml` it's `webservice`, on the diagram it's HTTP API.  
* There are no unit tests for `OrderStatusDecisionRoute` and `MLOrderStatusRecommendationService`
* There are no unit tests for batch_recommender
* There is an issue with the order of running the components. It takes some time for `feature_calculator`
  to compute all features. Only after the features get computed, `batch_recommender` can start sending requests to the api. This issue is partly
  solved by `depends_on` feature in docker compose. The thing is, it's impossible for one component to easy wait until another component is ready without using third-party scripts.
  That is why `batch_recommender` sleeps for 10 seconds before starting to actually work. Quite a hacky solution introduced due to lack of time to explore more elegant alternatives.
