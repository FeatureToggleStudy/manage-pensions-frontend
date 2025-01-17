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

package views.deregister

import forms.deregister.ConfirmStopBeingPsaFormProvider
import play.api.data.Form
import views.behaviours.YesNoViewBehaviours
import views.html.deregister.confirmStopBeingPsa

class ConfirmStopBeingPsaViewSpec extends YesNoViewBehaviours {

  private val messageKeyPrefix = "confirmStopBeingPsa"

  val formProvider = new ConfirmStopBeingPsaFormProvider
  val form: Form[Boolean] = formProvider()

  private def createView() =
    () => confirmStopBeingPsa(
      frontendAppConfig,
      form,
      "psaName"
    )(fakeRequest, messages)

  private def createViewUsingForm =
    (form: Form[_]) => confirmStopBeingPsa(
      frontendAppConfig,
      form,
      "psaName"
    )(fakeRequest, messages)

  "Confirm Stop Being Psa view" must {
    behave like normalPage(createView(), messageKeyPrefix, messages(s"messages__${messageKeyPrefix}__title"),"p1", "p2")

    behave like pageWithReturnLink(createView(), frontendAppConfig.registeredPsaDetailsUrl, messages("messages__returnToPsaDetails__link", "psaName"))

    behave like pageWithSubmitButton(createView())

    behave like yesNoPage(createViewUsingForm, messageKeyPrefix, "/")

  }
}

