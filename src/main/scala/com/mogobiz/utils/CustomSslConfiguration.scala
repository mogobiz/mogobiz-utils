/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.utils

import java.security.cert.X509Certificate
import javax.net.ssl.{KeyManager, SSLContext, X509TrustManager}

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.model.Uri.Host
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.io.IO
import akka.pattern.ask
import akka.stream.scaladsl.{Sink, Source}
import akka.util.Timeout
import com.mogobiz.system.ActorSystemLocator
import com.typesafe.sslconfig.akka.AkkaSSLConfig

import scala.concurrent.{Await, Future}

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

  implicit def timeout: Timeout

  val badSslConfig = AkkaSSLConfig().mapSettings(s => s.withLoose(s.loose.withDisableSNI(true)))
  val badCtx = Http().createClientHttpsContext(badSslConfig)
  val connectionFlow = Http().outgoingConnectionHttps(unsafeHost, connectionContext = badCtx)

  def httpsRequest(request: HttpRequest): Future[HttpResponse] = Source.single(request).via(connectionFlow).runWith(Sink.head)

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
}
