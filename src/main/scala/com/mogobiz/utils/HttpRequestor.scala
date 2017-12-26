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
/*
      val request = HttpRequest(
      method = HttpMethods.POST,
      uri = Uri(route(url)),
      entity = HttpEntity(MediaTypes.`application/json`, mapping)
    )
    val singleResult: Future[Unit] = Http().singleRequest(request).map { response: HttpResponse =>

      response.status match {
        case StatusCodes.OK => System.err.println(s"The mapping for `$name` was successfully set.")

        case _ =>
          // System.err.println(s"Error while setting the mapping for `$name`: ${response.entity.toStrict(5 seconds).map(_.data.toString())}")
          Unmarshal(response.entity).to[String].map { data =>
            System.err.println(s"Error while setting the mapping for `$name`: ${data}")
          }
      }
    }
    Await.result(singleResult, 10 seconds)


  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  // WARNING: disabling SNI is a very bad idea, please don't unless you have a very good reason to.
  val badSslConfig = AkkaSSLConfig().mapSettings(s => s.withLoose(s.loose.withDisableSNI(true)))
  val badCtx = Http().createClientHttpsContext(badSslConfig)
  Http().outgoingConnectionHttps(unsafeHost, connectionContext = badCtx)

 */

/*
trait CustomSslConfiguration {
  implicit val trustfulSslContext: SSLContext = {
    object BlindFaithX509TrustManager extends X509TrustManager {
      def checkClientTrusted(chain: Array[X509Certificate], authType: String) = ()

      def checkServerTrusted(chain: Array[X509Certificate], authType: String) = ()

      def getAcceptedIssuers = Array[X509Certificate]()
    }

    val context = SSLContext.getInstance("TLS")
    context.init(Array[KeyManager](), Array(BlindFaithX509TrustManager), null)
    context
  }

  implicit val sslEngineProvider: ClientSSLEngineProvider = {
    // To enable TLS 1.2 you can use the following setting of the VM: -Dhttps.protocols=TLSv1.1,TLSv1.2
    ClientSSLEngineProvider { engine =>
      engine.setEnabledProtocols(Array("TLSv1.2", "TLSv1.1", "SSLv3"))
      engine
    }
  }
  implicit def timeout: Timeout

  def sslPipeline(host: Host): Future[SendReceive] = {
    implicit val system = ActorSystemLocator()
    implicit val _ = system.dispatcher
    val logRequest: HttpRequest => HttpRequest = { r => println(r); r }
    val logResponse: HttpResponse => HttpResponse = { r => println(r); r }
    for (
      Http.HostConnectorInfo(connector, _) <- IO(Http) ? Http.HostConnectorSetup(host.toString, 443, sslEncryption = true)(system, sslEngineProvider)
    ) yield logRequest ~> sendReceive(connector) ~> logResponse

  }
}

 */
