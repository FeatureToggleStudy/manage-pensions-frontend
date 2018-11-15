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

package controllers.testonly

import config.FrontendAppConfig
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.ExecutionContext.Implicits.global

class FeaturesController @Inject()(config: FrontendAppConfig) extends FrontendController {

  def toggles() = Action {

    Ok(config.features.toggles.map(toggle => s"${toggle.name} = ${toggle.enabled}").mkString("\n"))

  }

  def enable(name: String) = Action {

    config.features.enable(name)
    Redirect(controllers.testonly.routes.FeaturesController.toggles())

  }

  def disable(name: String) = Action {

    config.features.disable(name)
    Redirect(controllers.testonly.routes.FeaturesController.toggles())

  }

  def disableAll(): Action[AnyContent] = Action.async {

    config.features.disableAll().map {
      _ =>
        Redirect(controllers.testonly.routes.FeaturesController.toggles())
    }

  }

  def reset() = Action {

    config.features.reset()
    Redirect(controllers.testonly.routes.FeaturesController.toggles())

  }

}
