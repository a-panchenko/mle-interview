package op.interview.ml.adapter.out

import op.interview.ml.core.domain.RecommendedOrderAction.RecommendedOrderAction
import op.interview.ml.core.domain.{Features, RecommendedOrderAction}
import op.interview.ml.port.out.OrderStatusRecommendationMLModel

import scala.concurrent.{ExecutionContext, Future}

class OrderStatusRecommendationMLModelV1(implicit ec: ExecutionContext) extends OrderStatusRecommendationMLModel {
  override def predict(features: Features): Future[RecommendedOrderAction] = Future {
    if (features.inventory <= 0)
      RecommendedOrderAction.HOLD_CHECK_AVAILABILITY
    else if (!features.zipCodeAvailable)
      RecommendedOrderAction.HOLD_CHECK_DELIVERY
    else if (features.paymentStatus != "OK")
      RecommendedOrderAction.HOLD_CHECK_PAYMENT
    else if (features.orderHourOfDay < 6)
      RecommendedOrderAction.DECLINE
    else RecommendedOrderAction.DELIVER
  }
}
