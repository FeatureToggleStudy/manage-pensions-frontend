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

import javax.inject.{Inject, Singleton}

import connectors.DataCacheConnector
import controllers.routes
import identifiers.{Identifier, PsaNameId}
import models.{CheckMode, NormalMode}
import play.api.mvc.Call
import utils.{UserAnswers, Navigator}

@Singleton
class InvitationNavigator @Inject()() extends Navigator {

  override def routeMap: PartialFunction[Identifier, UserAnswers => Call] = {
    case PsaNameId => addInvitationRoutes()
  }

  override protected def editRouteMap: PartialFunction[Identifier, UserAnswers => Call] = {
    case _ => addInvitationRoutes()
  }

  private def addInvitationRoutes()(answers: UserAnswers): Call = {

    answers.get(PsaNameId) match {
      case Some(_) =>
        controllers.routes.IndexController.onPageLoad()
      case None =>
        controllers.routes.SessionExpiredController.onPageLoad()
    }
  }
}
