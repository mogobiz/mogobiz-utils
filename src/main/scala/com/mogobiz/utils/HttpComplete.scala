package com.mogobiz.utils

import java.net.UnknownHostException

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import org.json4s.{DefaultFormats, jackson}
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._

import scala.util.{Failure, Success, Try}

trait HttpComplete {

  this: Directives =>

  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats

  def handleCall[T](call: => T, handler: T => Route): Route = {
    Try(call) match {
      case Success(res) => handler(res)
      case Failure(t)   => completeException(t)
    }
  }

  def completeException(t: Throwable): Route = {
    t match {
      case (ex: UnknownHostException) =>
        ex.printStackTrace()
        complete(
          StatusCodes.NotFound -> Map('type -> ex.getClass.getSimpleName,
                                      'error -> ex.getMessage))
      case (_) =>
        t.printStackTrace()
        complete(
          StatusCodes.InternalServerError -> Map(
            'type -> t.getClass.getSimpleName,
            'error -> t.getMessage))
    }
  }

  def handleComplete[T](call: Try[Try[T]], handler: T => Route): Route = {
    call match {
      case Failure(t) => completeException(t)
      case Success(res) =>
        res match {
          case Success(id) => handler(id)
          case Failure(t)  => completeException(t)
        }
    }
  }
}
