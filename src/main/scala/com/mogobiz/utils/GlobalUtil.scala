package com.mogobiz.utils

import java.io.File
import java.net.{URLDecoder, URLEncoder}
import java.util
import java.util.jar.{JarEntry, JarFile}

import scala.concurrent.{ExecutionContext, Future}
import spray.http.HttpResponse

import scala.util.{Success, Failure}

object GlobalUtil {
  def now = new java.util.Date()

  def newUUID = java.util.UUID.randomUUID().toString

  // From: http://stackoverflow.com/a/1227643/604041
  def caseClassToMap(cc: AnyRef) =
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
    m.map { case (k, v) =>
      println(s"$k=$v")
      s"$k=" + URLEncoder.encode(if (v == null) "" else v.toString, "UTF-8")
    }.mkString("&")
  }

  def mapToQueryStringNoEncode(m: List[(String, Any)]): String = {
    m.map { case (k, v) =>
      println(s"$k=$v")
      s"$k=$v"
    }.mkString("&")
  }

  def fromHttResponse(response: Future[HttpResponse])(implicit ev: ExecutionContext): Future[Map[String, String]] = {
    response map { response =>
      val data = response.entity.asString.trim
      println(s"data $data")
      val pairs = data.split('&')
      val tuples = (pairs map { pair =>
        val tab = pair.split('=')
        tab(0) -> (if (tab.length == 1) "" else tab(1))
      }).toMap
      tuples
    }
  }

  def hideStringExceptLastN(s: String, n: Int, replacement: String = "*") = {
    require(n >= 0, "n cannot be < 0.")
    require(n <= s.length, "n cannot be < 0.")
    println("foo")
    if (s == "") {
      ""
    } else {
      val nToHide = s.length - n
      (replacement * nToHide) + s.substring(nToHide)
    }
  }
}
