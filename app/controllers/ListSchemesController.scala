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

package controllers

import com.google.inject.Inject
import config.FrontendAppConfig
import connectors.ListOfSchemesConnector
import controllers.actions.AuthAction
import models.SchemeDetail
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.list_schemes

class ListSchemesController @Inject()(
                                       val appConfig: FrontendAppConfig,
                                       val messagesApi: MessagesApi,
                                       authenticate: AuthAction,
                                       listSchemesConnector: ListOfSchemesConnector
                                     ) extends FrontendController with I18nSupport {

  def onPageLoad: Action[AnyContent] = authenticate.async {
    implicit request =>
      listSchemesConnector.getListOfSchemes(request.psaId.id).map {
        listOfSchemes =>
          val schemes = listOfSchemes.schemeDetail.getOrElse(List.empty[SchemeDetail])
          Ok(list_schemes(appConfig, schemes))
      }
  }
}