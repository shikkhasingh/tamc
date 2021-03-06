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

package connectors

import java.net.URLEncoder
import scala.annotation.implicitNotFound
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import models.DesCreateRelationshipRequest
import models.FindRecipientRequest
import play.api.libs.json.JsValue
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.config.ServicesConfig
import utils.WSHttp
import models.Cid
import models.DesUpdateRelationshipRequest
import models.MultiYearDesCreateRelationshipRequest
import uk.gov.hmrc.http.{ HeaderCarrier, HttpGet, HttpPost, HttpPut, HttpResponse }
import uk.gov.hmrc.http.logging.Authorization

object MarriageAllowanceDataConnector extends MarriageAllowanceDataConnector with ServicesConfig {
  override val httpGet = WSHttp
  override val httpPost = WSHttp
  override val httpPut = WSHttp
  override val serviceUrl = baseUrl("marriage-allowance-des")
  override val urlHeaderEnvironment = config("marriage-allowance-des").getString("environment").get
  override val urlHeaderAuthorization = s"Bearer ${config("marriage-allowance-des").getString("authorization-token").get}"
}

trait MarriageAllowanceDataConnector {

  val httpGet: HttpGet
  val httpPost: HttpPost
  val httpPut: HttpPut
  val serviceUrl: String
  val urlHeaderEnvironment: String
  val urlHeaderAuthorization: String
  def url(path: String) = s"$serviceUrl$path"

  private def createHeaderCarrier = HeaderCarrier(extraHeaders = Seq(("Environment" -> urlHeaderEnvironment)), authorization = Some(Authorization(urlHeaderAuthorization)))

  def findCitizen(nino: Nino)(implicit ec: ExecutionContext): Future[JsValue] = {
    implicit val hc = createHeaderCarrier
    val path = url(s"/marriage-allowance/citizen/${nino}")
    httpGet.GET[JsValue](path)
  }

  def listRelationship(cid: Cid, includeHistoric: Boolean = true)(implicit ec: ExecutionContext): Future[JsValue] = {
    implicit val hc = createHeaderCarrier
    val path = url(s"/marriage-allowance/citizen/${cid}/relationships?includeHistoric=${includeHistoric}")
    httpGet.GET[JsValue](path)
  }

  def findRecipient(findRecipientRequest: FindRecipientRequest)(implicit ec: ExecutionContext): Future[JsValue] = {
    implicit val hc = createHeaderCarrier
    val query = s"surname=${utils.encodeQueryStringValue(findRecipientRequest.lastName)}&forename1=${utils.encodeQueryStringValue(findRecipientRequest.name)}&gender=${utils.encodeQueryStringValue(findRecipientRequest.gender.gender)}"
    val nino = findRecipientRequest.nino.nino.replaceAll(" ", "")
    val path = url(s"/marriage-allowance/citizen/${nino}/check?${query}")
    httpGet.GET[JsValue](path)
  }

  def sendMultiYearCreateRelationshipRequest(relType: String, createRelationshipRequest: MultiYearDesCreateRelationshipRequest)(implicit ec: ExecutionContext): Future[HttpResponse] = {
    implicit val hc = createHeaderCarrier
    val path = url(s"/marriage-allowance/02.00.00/citizen/${createRelationshipRequest.recipientCid}/relationship/${relType}")
    httpPost.POST(path, createRelationshipRequest)
  }

  def updateAllowanceRelationship(updateRelationshipRequest: DesUpdateRelationshipRequest)(implicit ec: ExecutionContext): Future[HttpResponse] = {
    implicit val hc = createHeaderCarrier
    val path = url(s"/marriage-allowance/citizen/${updateRelationshipRequest.participant1.instanceIdentifier}/relationship")
    httpPut.PUT(path, updateRelationshipRequest)
  }
}
