package op.interview.ml.port.out

import op.interview.ml.core.domain.Features
import op.interview.ml.core.domain.RecommendedOrderAction.RecommendedOrderAction

import scala.concurrent.Future

trait OrderStatusRecommendationMLModel {
  def predict(features: Features): Future[RecommendedOrderAction]
}
