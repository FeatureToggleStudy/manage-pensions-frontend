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

package forms.mappings

import forms.behaviours.EmailBehaviours
import play.api.data.{Form, Mapping}

class EmailMappingSpec extends EmailBehaviours {

  val requiredKey = "messages__error__adviser__email__address__required"
  val maxLengthKey = "messages__error__adviser__email__address__length"
  val invalidKey = "messages__error__adviser__email__address__invalid"

  val mapping: Mapping[String] = emailMapping(requiredKey, maxLengthKey, invalidKey)

  val fieldName = "email"

  val form = Form(
    fieldName -> mapping
  )

  behave like formWithEmailField(form, fieldName, requiredKey, maxLengthKey, invalidKey)

}
