/*
 * Copyright 2019 HM Revenue & Customs
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

import connectors.UserAnswersCacheConnector
import controllers.routes._
import identifiers.SchemeSrnId
import identifiers.remove.{ConfirmRemovePsaId, RemovalDateId}
import javax.inject.{Inject, Singleton}
import utils.{Navigator, UserAnswers}

@Singleton
class RemovePSANavigator @Inject()(val dataCacheConnector: UserAnswersCacheConnector) extends Navigator {

  override def routeMap(from: NavigateFrom): Option[NavigateTo] = from.id match {
    case ConfirmRemovePsaId => confirmRemovePsaRoutes(from.userAnswers)
    case RemovalDateId => NavigateTo.dontSave(controllers.remove.routes.ConfirmRemovedController.onPageLoad())
    case _ => NavigateTo.dontSave(controllers.routes.SessionExpiredController.onPageLoad())
  }

  private def confirmRemovePsaRoutes(userAnswers: UserAnswers) = {
    (userAnswers.get(ConfirmRemovePsaId), userAnswers.get(SchemeSrnId)) match {
      case (Some(false), Some(srn)) =>
        NavigateTo.dontSave(controllers.routes.SchemeDetailsController.onPageLoad(srn))
      case (Some(true), _) =>
        NavigateTo.dontSave(controllers.remove.routes.RemovalDateController.onPageLoad())
      case _ =>
        NavigateTo.dontSave(controllers.routes.SessionExpiredController.onPageLoad())
    }
  }

  override protected def editRouteMap(from: NavigateFrom): Option[NavigateTo] = from.id match {
    case _ => NavigateTo.dontSave(SessionExpiredController.onPageLoad())
  }
}
