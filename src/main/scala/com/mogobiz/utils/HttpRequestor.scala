/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.utils

import akka.actor.ActorSystem
import akka.http.scaladsl.{Http, HttpsConnectionContext}
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.mogobiz.system.ActorSystemLocator
import com.typesafe.sslconfig.akka.AkkaSSLConfig

import scala.concurrent.Future

trait HttpRequestor {
  implicit val system: ActorSystem = ActorSystemLocator()
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val badSslConfig: AkkaSSLConfig = AkkaSSLConfig().mapSettings(
    s =>
      s.withLoose(
        s.loose
          .withAcceptAnyCertificate(true)
          .withDisableHostnameVerification(true)
          .withAllowLegacyHelloMessages(Some(true))
          .withAllowUnsafeRenegotiation(Some(true))
          .withAllowWeakCiphers(true)
          .withAllowWeakProtocols(true)
          .withDisableSNI(true)))

  val badSslCtx: HttpsConnectionContext =
    Http().createClientHttpsContext(badSslConfig)

  def doRequest(request: HttpRequest): Future[HttpResponse] = {
    val uri = request.uri
    val isHttps = uri.scheme.toLowerCase().equals("https")
    val port =
      if (uri.authority.port != 0)
        uri.authority.port
      else if (isHttps)
        443
      else
        80
    if (isHttps) {
      val connectionFlow = Http().outgoingConnectionHttps(
        host = uri.authority.host.address(),
        port = port,
        connectionContext = badSslCtx)
      Source.single(request).via(connectionFlow).runWith(Sink.head)
    } else {
      Http().singleRequest(request)
    }
  }
}

object HttpRequestor extends HttpRequestor
