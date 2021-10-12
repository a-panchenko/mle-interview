package op.interview.ml.port.in

import op.interview.ml.core.domain.Order
import op.interview.ml.core.domain.RecommendedOrderAction.RecommendedOrderAction
import op.interview.ml.exception.BusinessException

import scala.concurrent.Future

trait OrderStatusRecommendationService {
  def evaluateOrder(order: Order): Future[Either[BusinessException, RecommendedOrderAction]]
}
