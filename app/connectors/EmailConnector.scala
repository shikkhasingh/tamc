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

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import models.SendEmailRequest
import uk.gov.hmrc.play.config.ServicesConfig
import utils.WSHttp
import uk.gov.hmrc.http.{ HeaderCarrier, HttpPost, HttpResponse }

object EmailConnector extends EmailConnector with ServicesConfig {
  override val httpPost = WSHttp
  override val emailUrl = baseUrl("email")
}

trait EmailConnector {

  val httpPost: HttpPost
  val emailUrl: String
  def url(path: String) = s"$emailUrl$path"

  def sendEmail(sendEmailRequest: SendEmailRequest)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] =
    httpPost.POST(url("/hmrc/email"), sendEmailRequest)
}
