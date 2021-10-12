package op.interview.ml.port.out

import op.interview.ml.core.domain.Features

import scala.concurrent.Future

trait FeaturesStorage {
  def loadFeatures(orderId: String): Future[Option[Features]]
}
