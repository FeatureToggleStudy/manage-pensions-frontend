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

package utils.navigators

import base.SpecBase
import identifiers.PsaNameId
import models.requests.IdentifiedRequest
import navigators.NavigatorBehaviour
import org.scalatest.OptionValues
import play.api.libs.json.Json
import play.api.mvc.Call
import uk.gov.hmrc.http.HeaderCarrier
import utils.{Enumerable, UserAnswers}

class InvitationNavigatorSpec extends SpecBase with NavigatorBehaviour {

  import InvitationNavigatorSpec._

  val navigator = new InvitationNavigator()

  private val routes = Table(
    ("Id", "User Answers", "Next Page (Normal Mode)", "Next Page (Check Mode)"),
    (PsaNameId, psaNameAnswers, psaNameCall, None),
    (PsaNameId, emptyAnswers, defaultCall, None)
  )

  navigator.getClass.getSimpleName must {
    appRunning()
    behave like navigatorWithRoutes(navigator, routes, dataDescriber)
  }
}

object InvitationNavigatorSpec extends OptionValues with Enumerable.Implicits {
  lazy val emptyAnswers = UserAnswers(Json.obj())
  lazy val psaNameAnswers = UserAnswers(Json.obj(PsaNameId.toString -> "answer"))
  lazy val psaNameCall: Call = controllers.routes.IndexController.onPageLoad()
  lazy val defaultCall: Call = controllers.routes.SessionExpiredController.onPageLoad()

  implicit val ex: IdentifiedRequest = new IdentifiedRequest() {
    val externalId: String = "test-external-id"
  }

  implicit val hc: HeaderCarrier = HeaderCarrier()

  private def dataDescriber(answers: UserAnswers): String = answers.toString

}
