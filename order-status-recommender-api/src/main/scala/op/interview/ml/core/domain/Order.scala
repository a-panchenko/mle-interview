package op.interview.ml.core.domain

import java.time.LocalDateTime

case class Order(
                  orderId: String,
                  customerId: String,
                  timestamp: LocalDateTime,
                  skuCode: String,
                  zipCode: String
                )
