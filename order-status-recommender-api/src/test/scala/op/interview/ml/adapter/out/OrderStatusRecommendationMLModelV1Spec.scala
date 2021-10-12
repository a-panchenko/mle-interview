package op.interview.ml.adapter.out

import op.interview.ml.core.domain.RecommendedOrderAction.RecommendedOrderAction
import op.interview.ml.core.domain.{Features, RecommendedOrderAction}
import org.scalatest.flatspec.AsyncFlatSpec

class OrderStatusRecommendationMLModelV1Spec extends AsyncFlatSpec {
  private val modelV1 = new OrderStatusRecommendationMLModelV1()

  "ModelV1" should "return HOLD_CHECK_AVAILABILITY if inventory < 1" in {
    test(defaultFeaturesHappy(inventory = -1), expectedResult = RecommendedOrderAction.HOLD_CHECK_AVAILABILITY)
    test(defaultFeaturesHappy(inventory = 0), expectedResult = RecommendedOrderAction.HOLD_CHECK_AVAILABILITY)
  }

  it should "return DELIVER if inventory >= 1" in {
    test(defaultFeaturesHappy(inventory = 1), expectedResult = RecommendedOrderAction.DELIVER)
  }

  it should "return HOLD_CHECK_DELIVERY if zip code is not available" in {
    test(defaultFeaturesHappy(zipCodeAvailable = false), expectedResult = RecommendedOrderAction.HOLD_CHECK_DELIVERY)
  }

  it should "return DELIVER if zip code is available" in {
    test(defaultFeaturesHappy(zipCodeAvailable = true), expectedResult = RecommendedOrderAction.DELIVER)
  }

  it should "return HOLD_CHECK_PAYMENT if payment status is not OK" in {
    test(defaultFeaturesHappy(paymentStatus = "FAILED"), expectedResult = RecommendedOrderAction.HOLD_CHECK_PAYMENT)
    test(defaultFeaturesHappy(paymentStatus = "VERIFY_ADDRESS"), expectedResult = RecommendedOrderAction.HOLD_CHECK_PAYMENT)
    test(defaultFeaturesHappy(paymentStatus = "VERIFY_BANK_DETAILS"), expectedResult = RecommendedOrderAction.HOLD_CHECK_PAYMENT)
  }

  it should "return DELIVER if payment status is OK" in {
    test(defaultFeaturesHappy(paymentStatus = "OK"), expectedResult = RecommendedOrderAction.DELIVER)
  }

  it should "return DECLINE if hour of day is < 6" in {
    test(defaultFeaturesHappy(orderHourOfDay = 0), expectedResult = RecommendedOrderAction.DECLINE)
  }

  it should "return DELIVER if hour of day is >= 6" in {
    test(defaultFeaturesHappy(orderHourOfDay = 7), expectedResult = RecommendedOrderAction.DELIVER)
  }


  private def test(features: Features, expectedResult: RecommendedOrderAction) = {
    modelV1.predict(features).map { recommendation =>
      assert(recommendation === expectedResult)
    }
  }

  private def defaultFeaturesHappy(orderHourOfDay: Int = 10,
                                   inventory: Int = 100,
                                   paymentStatus: String = "OK",
                                   zipCodeAvailable: Boolean = true
                                  ): Features = Features(orderHourOfDay, inventory, paymentStatus, zipCodeAvailable)
}
