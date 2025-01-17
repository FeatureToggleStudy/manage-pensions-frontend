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

package controllers.invitations

import javax.inject.Inject
import config.FrontendAppConfig
import connectors.UserAnswersCacheConnector
import controllers.actions._
import forms.invitations.PsaNameFormProvider
import identifiers.invitations.InviteeNameId
import models.Mode
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.annotations.Invitation
import utils.{Navigator, UserAnswers}
import views.html.invitations.psaName

import scala.concurrent.{ExecutionContext, Future}

class PsaNameController @Inject()(appConfig: FrontendAppConfig,
                                   override val messagesApi: MessagesApi,
                                   dataCacheConnector: UserAnswersCacheConnector,
                                   @Invitation navigator: Navigator,
                                   authenticate: AuthAction,
                                   getData: DataRetrievalAction,
                                   requireData: DataRequiredAction,
                                   formProvider: PsaNameFormProvider
                                 )(implicit val ec: ExecutionContext) extends FrontendController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode) = (authenticate andThen getData).async {
    implicit request =>

      val value = request.userAnswers.flatMap(_.get(InviteeNameId))
      val preparedForm = value.fold(form)(form.fill)

      Future.successful(Ok(psaName(appConfig, preparedForm, mode)))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (authenticate andThen getData).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(psaName(appConfig, formWithErrors, mode))),

        (value) => {
          dataCacheConnector.save(request.externalId, InviteeNameId, value).map(
            cacheMap =>
              Redirect(navigator.nextPage(InviteeNameId, mode, UserAnswers(cacheMap)))
          )
        }
      )
  }
}
