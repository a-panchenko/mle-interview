package op.interview.ml.adapter.out.storage

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import io.circe.Decoder
import io.circe.generic.semiauto._
import io.circe.parser.{decode, parse}
import op.interview.ml.core.domain.Features
import op.interview.ml.port.out.FeaturesStorage

import scala.concurrent.{ExecutionContext, Future}
import scala.io.{BufferedSource, Source}

class LocalFileFeaturesStorage(config: Config)(implicit ec: ExecutionContext) extends FeaturesStorage with LazyLogging {
  private[out] lazy val features = loadFeatures()

  override def loadFeatures(orderId: String): Future[Option[Features]] = features.map(_.get(orderId))


  private def loadFeatures() = Future {
    val bs = if (config.hasPath("featuresFile")) {
      Source.fromFile(config.getString("featuresFile"))
    } else {
      Source.fromInputStream(this.getClass.getClassLoader.getResourceAsStream("features"))
    }
    parseFeaturesMap(bs)
  }

  private def parseFeaturesMap(bs: BufferedSource) = {
    bs.getLines()
      .flatMap(decodeLine)
      .map(f => f.order_id -> convertToFeatures(f))
      .toMap
  }

  private def convertToFeatures(featuresLine: FeaturesLine): Features = {
    Features(featuresLine.order_hour_of_day, featuresLine.inventory, featuresLine.payment_status, featuresLine.zip_code_available)
  }

  private def decodeLine(line: String) = {
    decode[FeaturesLine](line) match {
      case Right(features) =>
        Some(features)
      case Left(error) =>
        logger.warn(s"Failed to parse FeaturesLine from $line because of error. The line will be ignored", error)
        None
    }
  }
}

private[out] case class FeaturesLine(order_id: String, inventory: Int, order_hour_of_day: Int, zip_code_available: Boolean, payment_status: String)

private[out] object  FeaturesLine {
  implicit lazy val featuresLineDecoder: Decoder[FeaturesLine] = deriveDecoder[FeaturesLine]
}
