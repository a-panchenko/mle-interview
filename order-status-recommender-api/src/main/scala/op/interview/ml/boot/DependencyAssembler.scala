package op.interview.ml.boot

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import com.typesafe.config.{Config, ConfigFactory}
import op.interview.ml.adapter.in.http.OrderStatusDecisionRoute
import op.interview.ml.adapter.out.OrderStatusRecommendationMLModelV1
import op.interview.ml.adapter.out.storage.LocalFileFeaturesStorage
import op.interview.ml.core.service.MLOrderStatusRecommendationService
import op.interview.ml.port.in.OrderStatusRecommendationService
import op.interview.ml.port.out.{FeaturesStorage, OrderStatusRecommendationMLModel}

import scala.concurrent.ExecutionContext

private[boot] object DependencyAssembler {
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val executionContext: ExecutionContext = actorSystem.dispatcher

  lazy val config: Config = ConfigFactory.load()

  private lazy val orderStatusDecisionMLModel: OrderStatusRecommendationMLModel = new OrderStatusRecommendationMLModelV1()
  private lazy val featuresStorage: FeaturesStorage = new LocalFileFeaturesStorage(config)
  private lazy val orderStatusDecisionService: OrderStatusRecommendationService = new MLOrderStatusRecommendationService(featuresStorage, orderStatusDecisionMLModel)

  private lazy val orderStatusDecisionEndpoint = new OrderStatusDecisionRoute(orderStatusDecisionService)

  val route: Route = orderStatusDecisionEndpoint.route

}
