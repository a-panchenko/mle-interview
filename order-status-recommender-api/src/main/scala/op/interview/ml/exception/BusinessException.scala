package op.interview.ml.exception

sealed abstract class BusinessException(message: String) extends RuntimeException(message)

case class FeaturesNotFound(orderId: String) extends BusinessException(s"Could not find features for the order $orderId")
