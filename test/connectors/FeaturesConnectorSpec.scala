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

package connectors

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import config.{InformDisable, InformDisableAll, InformEnable, InformReset}
import org.scalatest.{AsyncFlatSpec, BeforeAndAfterAll, BeforeAndAfterEach, Matchers}
import play.api.http.Status._
import play.api.inject.guice.GuiceApplicationBuilder

class FeaturesConnectorSpec extends AsyncFlatSpec with Matchers with BeforeAndAfterAll with BeforeAndAfterEach {

  protected val server: WireMockServer = new WireMockServer(wireMockConfig().dynamicPort())

  private lazy val app = new GuiceApplicationBuilder()
    .configure(
      "microservice" -> Map(
        "services" -> Map(
          "fake-service" -> Map(
            "host" -> "localhost",
            "port" -> server.port(),
            "base-url" -> "/fake-base-url"
          )
        )
      )
    )
    .build()

  "FeaturesConnector" should "inform of enable feature toggle events" in {

    server.stubFor(
      get(urlEqualTo("/fake-base-url/test-only/toggles/fake-feature/enable"))
        .willReturn(
          aResponse()
            .withStatus(OK)
        )
    )

    app.injector.instanceOf[FeaturesConnector].inform("fake-service", InformEnable("fake-feature")).map {
      _ =>
        succeed
    }

  }

  it should "inform of disable feature toggle events" in {

    server.stubFor(
      get(urlEqualTo("/fake-base-url/test-only/toggles/fake-feature/disable"))
        .willReturn(
          aResponse()
            .withStatus(OK)
        )
    )

    app.injector.instanceOf[FeaturesConnector].inform("fake-service", InformDisable("fake-feature")).map {
      _ =>
        succeed
    }

  }

  it should "inform of disable all feature toggle events" in {

    server.stubFor(
      get(urlEqualTo("/fake-base-url/test-only/toggles/disable-all"))
        .willReturn(
          aResponse()
            .withStatus(OK)
        )
    )

    app.injector.instanceOf[FeaturesConnector].inform("fake-service", InformDisableAll).map {
      _ =>
        succeed
    }

  }

  it should "inform of reset feature toggle events" in {

    server.stubFor(
      get(urlEqualTo("/fake-base-url/test-only/toggles/reset"))
        .willReturn(
          aResponse()
            .withStatus(OK)
        )
    )

    app.injector.instanceOf[FeaturesConnector].inform("fake-service", InformReset).map {
      _ =>
        succeed
    }

  }

  it should "throw RuntimeExceptrion if any call returns a status other than OK" in {

    server.stubFor(
      get(urlMatching(".*"))
        .willReturn(
          aResponse()
            .withStatus(BAD_REQUEST)
        )
    )

    recoverToSucceededIf[RuntimeException] {
      app.injector.instanceOf[FeaturesConnector].inform("fake-service", InformReset)
    }

  }

  override def beforeAll(): Unit = {
    server.start()
  }

  override def beforeEach(): Unit = {
    server.resetAll()
  }

  override def afterAll(): Unit = {
    if (server.isRunning) {
      server.stop()
    }
  }

}
