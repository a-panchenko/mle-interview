package op.interview.ml.adapter.out.storage

import com.typesafe.config.ConfigFactory
import op.interview.ml.core.domain.Features
import org.scalatest.flatspec.AsyncFlatSpec

class LocalFileFeaturesStorageSpec extends AsyncFlatSpec {
  private val conf = ConfigFactory.load()
  private val featuresStorage = new LocalFileFeaturesStorage(conf)
  private val expectedFeatures = Map(
    "1" -> Features(11, 10, "OK", zipCodeAvailable = true),
    "2" -> Features(12, 20, "OK", zipCodeAvailable = false),
    "3" -> Features(13, 30, "FAILED", zipCodeAvailable = false)
  )

  "LocalFileFeaturesStorage" should "load all expected features" in {
    featuresStorage.features.map { features =>
      assert(features == expectedFeatures)
    }
  }

  it should "return features by order id 1" in {
    featuresStorage.loadFeatures("1").map { featuresOpt =>
      assert(featuresOpt == expectedFeatures.get("1"))
    }
  }

  it should "return features by order id 2" in {
    featuresStorage.loadFeatures("2").map { featuresOpt =>
      assert(featuresOpt == expectedFeatures.get("2"))
    }
  }

  it should "return features by order id 3" in {
    featuresStorage.loadFeatures("3").map { featuresOpt =>
      assert(featuresOpt == expectedFeatures.get("3"))
    }
  }

  it should "return None by order id 4" in {
    featuresStorage.loadFeatures("4").map { featuresOpt =>
      assert(featuresOpt == expectedFeatures.get("4"))
    }
  }
}
