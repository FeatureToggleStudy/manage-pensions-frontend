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

import views.behaviours.ViewBehaviours
import views.html.deregister.successful_deregistration

class SuccessfulDeregistrationViewSpec extends ViewBehaviours {

  "SuccessfulDeregistration view" must {
    val messageKeyPrefix = "deregisterSuccess"

    def createView() = () => successful_deregistration(frontendAppConfig)(fakeRequest, messages)

    behave like normalPage(createView(), messageKeyPrefix, messages(s"messages__${messageKeyPrefix}__title"),"p1")

    "have a link to 'exit to gov uk'" in {
      createView must haveLink(url = frontendAppConfig.govUkLink, linkId = "gov-uk-link")
    }

  }

}
