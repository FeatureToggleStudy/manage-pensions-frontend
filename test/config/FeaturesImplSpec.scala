/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package config

import org.scalatest.{FlatSpec, Matchers}
import play.api.Configuration
import utils.FakeFeaturesConnector

class FeaturesImplSpec extends FlatSpec with Matchers {

  import FeaturesImplSpec._

  "Features" should "configure an empty list of feature toggles if there is no configuration" in {

    val features = new FeaturesImpl(Configuration(), new FakeFeaturesConnector())

    features.toggles shouldBe empty

  }

  it should "configure an empty list of feature toggles if there is empty configuration" in {

    val features = new FeaturesImpl(
      Configuration(
        "x-features.toggles" -> Map.empty
      ),
      new FakeFeaturesConnector()
    )

    features.toggles shouldBe empty

  }

  it should "configure the correct list of feature toggles when there is configuration" in {

    val features = new FeaturesImpl(
      Configuration(
        "x-features" -> Map(
          "toggles" -> Map(
            WP1 -> true,
            WP2 -> false
          )
        )
      ),
      new FakeFeaturesConnector()
    )

    features.toggles should contain allOf(FeatureToggle(WP1, true), FeatureToggle(WP2, false))

  }

  it should "configure an empty list of informed services if there is no configuration" in {

    val features = new FeaturesImpl(Configuration(), new FakeFeaturesConnector())

    features.services shouldBe empty

  }

  it should "configure an empty list of informed services if there is empty configuration" in {

    val features = new FeaturesImpl(
      Configuration(
        "x-features.informed-services" -> Seq.empty
      ),
      new FakeFeaturesConnector()
    )

    features.services shouldBe empty

  }

  it should "configure the correct list of informed services when there is configuration" in {

    val features = new FeaturesImpl(
      Configuration(
        "x-features" -> Map(
          "informed-services" -> List(
            SERVICE1,
            SERVICE2
          )
        )
      ),
      new FakeFeaturesConnector()
    )

    features.services should contain allOf(SERVICE1, SERVICE2)

  }

  it should "configure both feature toggles and informed services" in {

    val features = new FeaturesImpl(
      Configuration(
        "x-features" -> Map(
          "toggles" -> Map(
            WP1 -> true,
            WP2 -> false
          ),
        "informed-services" -> List(
          SERVICE1,
          SERVICE2
        )
        )
      ),
      new FakeFeaturesConnector()
    )

    features.toggles should contain allOf(FeatureToggle(WP1, true), FeatureToggle(WP2, false))
    features.services should contain allOf(SERVICE1, SERVICE2)

  }

}

object FeaturesImplSpec {

  val WP1 = "wp1"
  val WP2 = "wp2"

  val SERVICE1 = "service1"
  val SERVICE2 = "service2"

}
