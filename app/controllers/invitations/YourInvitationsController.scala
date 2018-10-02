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

package controllers.invitations

import config.FrontendAppConfig
import connectors.{InvitationsCacheConnector, UserAnswersCacheConnector}
import controllers.actions._
import identifiers.SchemeSrnId
import javax.inject.Inject
import models.{NormalMode, SchemeReferenceNumber}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.annotations.{AcceptInvitation, Invitation}
import utils.{Navigator, UserAnswers}
import views.html.invitations.yourInvitations

class YourInvitationsController @Inject()(appConfig: FrontendAppConfig,
                                  override val messagesApi: MessagesApi,
                                  authenticate: AuthAction,
                                          getData: DataRetrievalAction,
                                          requireData: DataRequiredAction,
                                  invitationsCacheConnector: InvitationsCacheConnector,
                                  userAnswersCacheConnector: UserAnswersCacheConnector,
                                  @AcceptInvitation navigator: Navigator
                                 ) extends FrontendController with I18nSupport {


  def onPageLoad(): Action[AnyContent] = (authenticate).async {
    implicit request =>
      println("\n\n\n onpage load: ")
      invitationsCacheConnector.getForInvitee(request.psaId.id).map {
        case Nil => Redirect(controllers.routes.SessionExpiredController.onPageLoad())
        case invitationsList => {
          println("\n\n\n invitationsList : "+invitationsList)
          Ok(yourInvitations(appConfig, invitationsList))
        }
      }
  }

  def onSubmit(srn: SchemeReferenceNumber): Action[AnyContent] = (authenticate).async {
    implicit request =>
      println("\n\n\n srn: "+srn.id)
      userAnswersCacheConnector.save(request.externalId, SchemeSrnId, srn.id).map { cacheMap =>
        Redirect(navigator.nextPage(SchemeSrnId, NormalMode, UserAnswers(cacheMap)))
      }
  }




}
