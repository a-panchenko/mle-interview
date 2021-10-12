package op.interview.ml.adapter.in.http

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.FutureDirectives
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.Decoder
import io.circe.generic.auto._
import op.interview.ml.exception.{BusinessException, FeaturesNotFound}
import op.interview.ml.port.in.OrderStatusRecommendationService

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class OrderStatusDecisionRoute(
                                orderStatusDecisionService: OrderStatusRecommendationService
                              )(implicit actorSystem: ActorSystem, executionContext: ExecutionContext) extends LazyLogging {

  private implicit val customTimestampDecoder: Decoder[LocalDateTime] = Decoder.decodeString
    .map(s => LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))

  val route: Route = path("decision" / "v1") {
    post {
      entity(as[DecisionRequestPayload]) { decisionRequestEntity =>
        val order = decisionRequestEntity.toOrder

        val responseF = orderStatusDecisionService.evaluateOrder(order).map {
          case Right(recommendation) =>
            complete(DecisionResponsePayload(order.orderId, recommendation))
          case Left(e: FeaturesNotFound) =>
            logger.debug(e.getMessage)
            complete((StatusCodes.NotFound, ErrorResponse(ErrorPayload("features_not_found", Some(e.getMessage)))))
          case Left(_: BusinessException) =>
            complete((StatusCodes.InternalServerError, ErrorResponse(ErrorPayload("unknown_business_error"))))
        }
        FutureDirectives.onComplete(responseF) {
          case Success(value) =>
            value
          case Failure(exception) =>
            logger.error("POST /decision/v1 failed due to an exception.", exception)
            complete((StatusCodes.InternalServerError, ErrorResponse(ErrorPayload("unknown_server_error"))))
        }
      }
    }
  }

}
