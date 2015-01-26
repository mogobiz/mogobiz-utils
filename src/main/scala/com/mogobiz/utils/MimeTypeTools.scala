package com.mogobiz.utils

import java.io.File

/**
 *
 * Created by smanciot on 26/01/15.
 */
object MimeTypeTools {

  def detectMimeType(file:File):Option[String] = {
    if(file != null && file.exists()){
        ExecuteShellTools.executeCommand(s"file --mime-type ${file.getPath}") match {
          case r"(.*:\s)(.*)$mimeType" => Some(mimeType)
          case _ => None
        }
    }
    else{
      None
    }
  }

  def toFormat(file:File):Option[String] = {
    toFormat(detectMimeType(file))
  }

  def toFormat(mimeType:Option[String]):Option[String] = {
    mimeType match {
      case Some(s) => s match {
        case r"(.*)\/(.*)${format}" => Some(format)
        case _ => None
      }
      case None => None
    }
  }
}
