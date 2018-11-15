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

import com.google.inject.{ImplementedBy, Inject}
import config.InformWhat
import play.api.{Configuration, Environment}
import play.api.Mode.Mode
import play.api.http.Status._
import play.api.libs.ws.WSClient
import uk.gov.hmrc.play.config.ServicesConfig

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@ImplementedBy(classOf[FeaturesConnectorImpl])
trait FeaturesConnector {

  def inform(service: String, what: InformWhat): Future[Unit]

}

class FeaturesConnectorImpl @Inject()(
  ws: WSClient,
  override val runModeConfiguration: Configuration,
  environment: Environment
) extends FeaturesConnector with ServicesConfig {

  override protected def mode: Mode = environment.mode

  override def inform(service: String, what: InformWhat): Future[Unit] = {

    val url = baseUrl(service) + getConfString(s"$service.base-url", "") + what.url

    ws.url(url).get().map {
      response =>
        if (response.status == OK) {
          ()
        } else {
          throw new RuntimeException(s"Cannot inform service of feature toggle change. Invalid status ${response.status} returned from $url")
        }
    }

  }

}
