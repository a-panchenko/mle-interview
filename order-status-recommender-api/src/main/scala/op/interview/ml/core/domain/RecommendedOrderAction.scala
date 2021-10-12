package op.interview.ml.core.domain

object RecommendedOrderAction extends Enumeration {
  type RecommendedOrderAction = Value
  val DELIVER, HOLD_CHECK_AVAILABILITY, HOLD_CHECK_DELIVERY, HOLD_CHECK_PAYMENT, DECLINE = Value
}
