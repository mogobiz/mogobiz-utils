/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.utils

import java.net.URLEncoder

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.{LazyLogging, Logger}
import scalikejdbc.TxBoundary.Try._
import scalikejdbc.{DB, DBSession}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object GlobalUtil extends LazyLogging {
  implicit val system: ActorSystem = akka.actor.ActorSystem("mogopay-boot")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  def now = new java.util.Date()

  def newUUID = java.util.UUID.randomUUID().toString

  // From: http://stackoverflow.com/a/1227643/604041
  def caseClassToMap(cc: AnyRef): Map[String, Any] =
    (Map[String, Any]() /: cc.getClass.getDeclaredFields) { (a, f) =>
      f.setAccessible(true)
      a + (f.getName -> f.get(cc))
    }

  def mapToQueryString(m: Map[String, Any]): String = {
    mapToQueryString(m.toList)
  }

  def mapToQueryStringNoEncode(m: Map[String, Any]): String = {
    mapToQueryStringNoEncode(m.toList)
  }

  def mapToQueryString(m: List[(String, Any)]): String = {
    m.map {
        case (k, v) =>
          logger.debug(s"$k=$v")
          s"$k=" + URLEncoder.encode(if (v == null) "" else v.toString, "UTF-8")
      }
      .mkString("&")
  }

  def mapToQueryStringNoEncode(m: List[(String, Any)]): String = {
    m.map {
        case (k, v) =>
          logger.debug(s"$k=$v")
          s"$k=$v"
      }
      .mkString("&")
  }

  def fromHttResponse(response: Future[HttpResponse])(
      implicit ev: ExecutionContext): Future[Map[String, String]] = {
    response flatMap { response =>
      Unmarshal(response.entity).to[String].map { data =>
        logger.debug(s"data $data")
        val pairs = data.trim.split('&')
        val tuples = (pairs map { pair =>
          val tab = pair.split('=')
          tab(0) -> (if (tab.length == 1) "" else tab(1))
        }).toMap
        tuples
      }
    }
  }

  def hideStringExceptLastN(s: String,
                            n: Int = 4,
                            replacement: String = "*") = {
    require(n >= 0, "The number of characters to hide cannot be lesser than 0.")
    if (s.length < n) {
      replacement * s.length
    } else {
      val nToHide = s.length - n
      (replacement * nToHide) + s.substring(nToHide)
    }
  }

  def queryStringToMap(s: String,
                       sep: String = "&",
                       elementsSep: String = "="): Map[String, String] = {
    val splitted = s.split(sep)
    splitted.toList match {
      case head :: Nil => Map()
      case split =>
        split.map { s =>
          val split = s.indexOf(elementsSep)
          (s.substring(0, split), s.substring(split + 1))
        }.toMap
    }
  }

  def runInTransaction[T, U](call: DBSession => T,
                             success: T => U,
                             failure: Throwable => Unit = { e: Throwable =>
                               }): U = {
    val r = DB localTx { implicit session =>
      Try {
        call(session)
      }
    }
    r match {
      case Success(o) => success(o)
      case Failure(e) => {
        failure(e)
        throw e
      }
    }
  }
}
