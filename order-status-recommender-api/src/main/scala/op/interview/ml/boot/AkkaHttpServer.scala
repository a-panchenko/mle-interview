package op.interview.ml.boot

import akka.http.scaladsl.Http
import com.typesafe.scalalogging.LazyLogging

import scala.util.{Failure, Success}

private[boot] object AkkaHttpServer extends LazyLogging {
  import DependencyAssembler._

  def start(): Unit = {
    val host = config.getString("http.host")
    val port = config.getInt("http.port")
    Http().newServerAt(host, port).bind(DependencyAssembler.route).onComplete {
      case Success(v) =>
        logger.info(s"Successfully started server on $host:$port...")
      case Failure(e) =>
        logger.error("Failed to started server at because of an exception", e)
    }
  }
}
