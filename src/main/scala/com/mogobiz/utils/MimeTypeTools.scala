package com.mogobiz.utils

import java.io.File
import java.util.regex.Pattern

/**
 *
 * Created by smanciot on 26/01/15.
 */
object MimeTypeTools {

  val MIME_TYPE = Pattern.compile("(.*:\\s)(.*)")

  val FORMAT = Pattern.compile("(.*)\\/(.*)")

  def detectMimeType(file:File):Option[String] = {
    if(file != null && file.exists()){
      val result = ExecuteShellTools.executeCommand(s"file --mime-type ${file.getPath}")
      val matcher = MIME_TYPE.matcher(result)
      if(matcher.find() && matcher.groupCount() > 1){
        Some(matcher.group(2))
      }
      else{
        None
      }
//      result match {
//        case r"(.*:\s)(.*)$mimeType" => Some(mimeType)
//        case _ => None
//      }
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
      case Some(s) =>
        val matcher = FORMAT.matcher(s)
        if(matcher.find() && matcher.groupCount() > 1){
          Some(matcher.group(2))
        }
        else{
          None
        }
//        s match {
//          case r"(.*)\/(.*)${format}" => Some(format)
//          case _ => None
//        }
      case None => None
    }
  }
}
