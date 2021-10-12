package op.interview.ml.core.domain

case class Features(
                     orderHourOfDay: Int,
                     inventory: Int,
                     paymentStatus: String,
                     zipCodeAvailable: Boolean
                   )
