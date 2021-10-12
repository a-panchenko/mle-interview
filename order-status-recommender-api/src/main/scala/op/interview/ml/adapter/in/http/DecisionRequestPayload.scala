package op.interview.ml.adapter.in.http

import op.interview.ml.core.domain.Order

import java.time.LocalDateTime

private[http] case class DecisionRequestPayload(
                                                 order_id: String,
                                                 customer_id: String,
                                                 timestamp: LocalDateTime,
                                                 sku_code: String,
                                                 zip_code: String
                                               ) {
  def toOrder: Order = Order(order_id, customer_id, timestamp, sku_code, zip_code)
}
