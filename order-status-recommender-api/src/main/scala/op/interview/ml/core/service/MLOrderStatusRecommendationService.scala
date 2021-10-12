package op.interview.ml.core.service

import op.interview.ml.core.domain.Order
import op.interview.ml.core.domain.RecommendedOrderAction.RecommendedOrderAction
import op.interview.ml.exception.{BusinessException, FeaturesNotFound}
import op.interview.ml.port.in.OrderStatusRecommendationService
import op.interview.ml.port.out.{FeaturesStorage, OrderStatusRecommendationMLModel}

import scala.concurrent.{ExecutionContext, Future}

class MLOrderStatusRecommendationService(
                                          featuresStorage: FeaturesStorage,
                                          orderStatusRecommendationMLModel: OrderStatusRecommendationMLModel
                                        )(implicit ec: ExecutionContext) extends OrderStatusRecommendationService {

  override def evaluateOrder(order: Order): Future[Either[BusinessException, RecommendedOrderAction]] = featuresStorage.loadFeatures(order.orderId)
    .flatMap {
      case Some(features) =>
        orderStatusRecommendationMLModel.predict(features).map(Right(_))
      case None =>
        Future.successful(Left(FeaturesNotFound(order.orderId)))
    }
}
