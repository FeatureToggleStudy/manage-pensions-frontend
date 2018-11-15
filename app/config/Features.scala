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

package config

import com.google.inject.ImplementedBy
import connectors.FeaturesConnector
import javax.inject.Inject
import play.api.{Configuration, Logger}

import scala.collection.JavaConversions._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class FeatureToggle(name: String, enabled: Boolean)

object FeatureToggle {

  implicit val ordering: Ordering[FeatureToggle] = new Ordering[FeatureToggle] {
    override def compare(x: FeatureToggle, y: FeatureToggle): Int = x.name.compare(y.name)
  }

}

@ImplementedBy(classOf[FeaturesImpl])
trait Features {

  def toggles: Seq[FeatureToggle]

  def services: Seq[String]

  def isEnabled(name: String): Boolean

  def enable(name: String): Unit

  def disable(name: String): Unit

  def disableAll(): Future[Unit]

  def reset(): Unit

}

class FeaturesImpl @Inject() (configuration: Configuration, connector: FeaturesConnector) extends Features {

  private[this] val lock = new Object()

  private[this] var featureToggles = readToggles()
  private[this] val informedServices = readServices()

  logFeatureConfiguration()

  override def toggles: Seq[FeatureToggle] = {

    lock.synchronized {
      featureToggles
    }

  }

  override def services: Seq[String] = {

    lock.synchronized {
      informedServices
    }

  }

  override def isEnabled(name: String): Boolean = {

    lock.synchronized {
      featureToggles.find(_.name == name).exists(_.enabled)
    }

  }

  override def enable(name: String): Unit = {

    lock.synchronized {
      informedServices.foreach(service => Logger.info(s"Telling $service about toggle change"))

      featureToggles = featureToggles.map {
        case FeatureToggle(`name`, _) => FeatureToggle(name, true)
        case toggle => toggle
      }

      logFeatureConfiguration()
    }

  }

  override def disable(name: String): Unit = {

    lock.synchronized {
      informedServices.foreach(service => Logger.info(s"Telling $service about toggle change"))

      featureToggles = featureToggles.map {
        case FeatureToggle(`name`, _) => FeatureToggle(name, false)
        case toggle => toggle
      }

      logFeatureConfiguration()
    }

  }

  override def disableAll(): Future[Unit] = {

    lock.synchronized {
      inform(InformDisableAll).map {
        _ =>
          featureToggles = featureToggles.map(_.copy(enabled = false))
          logFeatureConfiguration()
      }
    }

  }

  override def reset(): Unit = {

    lock.synchronized {
      informedServices.foreach(service => Logger.info(s"Resetting $service"))

      featureToggles = readToggles()

      logFeatureConfiguration()
    }

  }

  private def readToggles(): Seq[FeatureToggle] =
    configuration.getObject("features.toggles").map {
      _.toConfig.entrySet().map {
        toggle =>
          val name = toggle.getKey
          val enabled = toggle.getValue.atKey(name).getBoolean(name)
          FeatureToggle(name, enabled)
      }.toSeq.sorted
    }.getOrElse(Seq.empty)

  private def readServices(): Seq[String] =
    configuration.getStringList("features.informed-services").map {
      _.map {
        identity
      }.toSeq
    }.getOrElse(Seq.empty)

  private def logFeatureConfiguration(): Unit = {

    Logger.warn(
      "Feature toggle configuration\n\n" +
        this.featureToggles.map(toggle => s"${toggle.name} = ${toggle.enabled}").mkString("\n")
    )

  }

  private def inform(what: InformWhat): Future[Unit] = {

    for {
      _ <- Future.traverse(services)(service => connector.inform(service, what))
    } yield {}

  }

}

sealed trait InformWhat {
  def url: String
}

case class InformEnable(name: String) extends InformWhat {
  override def url: String = s"/test-only/toggles/$name/enable"
}

case class InformDisable(name: String) extends InformWhat {
  override def url: String = s"/test-only/toggles/$name/disable"
}

case object InformDisableAll extends InformWhat {
  override def url: String = s"/test-only/toggles/disable-all"
}

case object InformReset extends InformWhat {
  override def url: String = s"/test-only/toggles/reset"
}
