/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.utils

import java.io.File

import org.scalatest.{FlatSpec, Matchers}

/**
  *
  */
class MimeTypeToolsSpec extends FlatSpec with Matchers {

  "MimeTypeTools" should "detect application/pdf mime type" in {
    val file = new File(MimeTypeTools.getClass.getResource("document").getPath)
    file exists() shouldBe true
    val mimeType = MimeTypeTools.detectMimeType(file).getOrElse("unknown")
    mimeType shouldBe "application/pdf"
    val format = MimeTypeTools.toFormat(file).getOrElse("unknown")
    format shouldBe "pdf"
  }
  it should "detect image/jpeg mime type" in {
    val file = new File(MimeTypeTools.getClass.getResource("image1").getPath)
    file exists() shouldBe true
    val mimeType = MimeTypeTools.detectMimeType(file).getOrElse("unknown")
    mimeType shouldBe "image/jpeg"
    val format = MimeTypeTools.toFormat(file).getOrElse("unknown")
    format shouldBe "jpeg"
  }
  it should "detect image/png mime type" in {
    val file = new File(MimeTypeTools.getClass.getResource("image2").getPath)
    file exists() shouldBe true
    val mimeType = MimeTypeTools.detectMimeType(file).getOrElse("unknown")
    mimeType shouldBe "image/png"
    val format = MimeTypeTools.toFormat(file).getOrElse("unknown")
    format shouldBe "png"
  }
}
