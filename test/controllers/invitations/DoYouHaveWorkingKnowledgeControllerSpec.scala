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

import connectors.FakeUserAnswersCacheConnector
import controllers.actions._
import controllers.behaviours.ControllerWithQuestionPageBehaviours
import forms.invitations.DoYouHaveWorkingKnowledgeFormProvider
import identifiers.invitations.DoYouHaveWorkingKnowledgeId
import models.NormalMode
import play.api.data.Form
import play.api.libs.json.Json
import play.api.test.FakeRequest
import utils.{FakeNavigator, UserAnswers}
import views.html.invitations.doYouHaveWorkingKnowledge
import utils.UserAnswerOps

class DoYouHaveWorkingKnowledgeControllerSpec extends ControllerWithQuestionPageBehaviours {

  val formProvider = new DoYouHaveWorkingKnowledgeFormProvider()
  val form = formProvider()
  val userAnswer = UserAnswers().employedPensionAdviserId(true)
  val postRequest = FakeRequest().withJsonBody(Json.obj("value" -> true))
  val data = UserAnswers().employedPensionAdviserId(true).dataRetrievalAction

  private def onPageLoadAction(dataRetrievalAction: DataRetrievalAction, fakeAuth: AuthAction) = {

  val controller = new DoYouHaveWorkingKnowledgeController(
    frontendAppConfig,
    FakeAuthAction(),
    messagesApi,
    FakeNavigator,
    formProvider,
    FakeUserAnswersCacheConnector,
    new FakeDataRetrievalAction(Some(Json.obj())),
    new DataRequiredActionImpl
  )
    new DoYouHaveWorkingKnowledgeController(
      frontendAppConfig, fakeAuth, messagesApi, navigator,formProvider,
      FakeUserAnswersCacheConnector, dataRetrievalAction, requiredDataAction).onPageLoad(NormalMode)
  }

  private def onSubmitAction(dataRetrievalAction: DataRetrievalAction, fakeAuth: AuthAction) = {

    new DoYouHaveWorkingKnowledgeController(
      frontendAppConfig, fakeAuth, messagesApi, navigator, formProvider,
      FakeUserAnswersCacheConnector, dataRetrievalAction, requiredDataAction).onSubmit(NormalMode)
  }

 def viewAsString(form: Form[Boolean] = form) = doYouHaveWorkingKnowledge(frontendAppConfig, form, NormalMode)(fakeRequest, messages).toString

  behave like controllerWithOnPageLoadMethod(onPageLoadAction, getEmptyData, userAnswer.dataRetrievalAction, form, form.fill(true), viewAsString)

  behave like controllerWithOnSubmitMethod(onSubmitAction, data,
    form.bind(Map("value" -> "")), viewAsString, postRequest)
}

