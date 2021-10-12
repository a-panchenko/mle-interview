package op.interview.ml.adapter.in.http

import io.circe._
import io.circe.generic.semiauto._
import op.interview.ml.core.domain.RecommendedOrderAction
import op.interview.ml.core.domain.RecommendedOrderAction.RecommendedOrderAction

private[http] case class DecisionResponsePayload(
                                                  order_id: String,
                                                  recommendation: RecommendedOrderAction
                                                )

object DecisionResponsePayload {
  implicit lazy val recommendedOrderAction: Encoder[RecommendedOrderAction.Value] = Encoder.encodeEnumeration(RecommendedOrderAction)
  implicit lazy val decisionResponsePayloadEncoder: Encoder.AsObject[DecisionResponsePayload] = deriveEncoder[DecisionResponsePayload]
}
