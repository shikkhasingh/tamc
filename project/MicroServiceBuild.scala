/*
 * Copyright 2015 HM Revenue & Customs
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

import play.sbt.routes.RoutesKeys
import sbt._

object MicroServiceBuild extends Build with MicroService {
  val appName = "tamc"
  override lazy val appDependencies: Seq[ModuleID] = AppDependencies()
  override lazy val playSettings = Seq(RoutesKeys.routesImport ++= Seq("binders._", "uk.gov.hmrc.domain._"))
}

private object AppDependencies {

  private val microserviceBootstrapVersion = "8.5.0"
  private val domainVersion = "5.2.0"
  private val hmrcTestVersion = "3.2.0"
  private val scalaTestVersion = "2.2.6"
  private val jsoupVersion = "1.8.3"
  private val timeVersion = "2.1.0"
  private val emailAddressVersion = "2.0.0"
  private val scalaTestPlusPlayVersion = "2.0.1"
  private val mockitoCoreVerison = "1.9.5"

  val compile = Seq(
    "uk.gov.hmrc" %% "domain" % domainVersion,
    "uk.gov.hmrc" %% "emailaddress" % emailAddressVersion,
    "uk.gov.hmrc" %% "microservice-bootstrap" % microserviceBootstrapVersion,
    "uk.gov.hmrc" %% "time" % timeVersion
  )

  trait TestDependencies {
    lazy val testScope: String = "test"
    lazy val test: Seq[ModuleID] = Seq.empty
  }

  object Test {
    def apply(): Seq[ModuleID] = new TestDependencies {
      override lazy val test: Seq[ModuleID] = Seq(
        "uk.gov.hmrc" %% "hmrctest" % hmrcTestVersion % testScope,
        "org.jsoup" % "jsoup" % jsoupVersion % testScope,
        "org.scalatest" %% "scalatest" % scalaTestVersion % testScope,
        "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestPlusPlayVersion % testScope,
        "org.mockito" % "mockito-core" % mockitoCoreVerison % testScope
      )
    }.test
  }

  def apply(): Seq[ModuleID] = compile ++ Test()
}
