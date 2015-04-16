package com.mogobiz.utils

import java.io.File

import org.specs2.mutable.Specification

/**
 *
 * Created by smanciot on 16/04/15.
 */
class MimeTypeToolsSpec extends Specification{

  "MimeTypeTools" should {
    "detect application/pdf mime type" in {
      val file = new File(MimeTypeTools.getClass.getResource("document").getPath)
      file exists() must_== true
      val mimeType = MimeTypeTools.detectMimeType(file).getOrElse("unknown")
      mimeType must_== "application/pdf"
      val format = MimeTypeTools.toFormat(file).getOrElse("unknown")
      format must_== "pdf"
    }
    "detect image/jpeg mime type" in {
      val file = new File(MimeTypeTools.getClass.getResource("image1").getPath)
      file exists() must_== true
      val mimeType = MimeTypeTools.detectMimeType(file).getOrElse("unknown")
      mimeType must_== "image/jpeg"
      val format = MimeTypeTools.toFormat(file).getOrElse("unknown")
      format must_== "jpeg"
    }
    "detect image/png mime type" in {
      val file = new File(MimeTypeTools.getClass.getResource("image2").getPath)
      file exists() must_== true
      val mimeType = MimeTypeTools.detectMimeType(file).getOrElse("unknown")
      mimeType must_== "image/png"
      val format = MimeTypeTools.toFormat(file).getOrElse("unknown")
      format must_== "png"
    }
  }

}
