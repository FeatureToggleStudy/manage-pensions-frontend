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

package utils

import config.{FeatureToggle, Features}

import scala.concurrent.Future

class FakeFeatures(override val toggles: Seq[FeatureToggle], override val services: Seq[String]) extends Features {

  override def isEnabled(name: String): Boolean = toggles.find(_.name == name).exists(_.enabled)

  override def enable(name: String): Unit = throw new NotImplementedError()

  override def disable(name: String): Unit = throw new NotImplementedError()

  override def disableAll(): Future[Unit] = throw new NotImplementedError()

  override def reset(): Unit = throw new NotImplementedError()

}

object FakeFeatures {

  def apply(): FakeFeatures = new FakeFeatures(Seq.empty, Seq.empty)

}
