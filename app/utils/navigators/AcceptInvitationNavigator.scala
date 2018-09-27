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
import connectors.UserAnswersCacheConnector
import controllers.routes
import identifiers.invitations._
import utils.Navigator

@Singleton
class AcceptInvitationNavigator @Inject()(val dataCacheConnector: UserAnswersCacheConnector) extends Navigator {

  override def routeMap(from: NavigateFrom): Option[NavigateTo] = from.id match {
    case PsaNameId | HaveYouEmployedPensionAdviserId => NavigateTo.save(routes.IndexController.onPageLoad())
    case AdviserNameId | AdviserAddressPostCodeLookupId => NavigateTo.dontSave(routes.IndexController.onPageLoad())
    case DeclarationId => NavigateTo.dontSave(routes.IndexController.onPageLoad())
    case _ => NavigateTo.dontSave(controllers.routes.SessionExpiredController.onPageLoad())
  }

  override protected def editRouteMap(from: NavigateFrom): Option[NavigateTo] = from.id match {
    case _ => NavigateTo.dontSave(controllers.routes.SessionExpiredController.onPageLoad())
  }
}