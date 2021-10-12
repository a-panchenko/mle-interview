package op.interview.ml.adapter.in.http

import io.circe.Encoder
import io.circe.generic.semiauto._

private[http] case class ErrorResponse(error: ErrorPayload)

private[http] object ErrorResponse {
  implicit lazy val errorPayloadEncoder: Encoder[ErrorPayload] = deriveEncoder[ErrorPayload]
  implicit lazy val errorResponseEncoder: Encoder[ErrorResponse] = deriveEncoder[ErrorResponse]
}

private[http] case class ErrorPayload(code: String, message: Option[String] = None)
