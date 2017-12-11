/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.utils

import com.mogobiz.utils.GlobalUtil._
import org.scalatest.{FlatSpec, Matchers}

class GlobalUtilSpec extends FlatSpec with Matchers {
  "caseClassToMap" should
    "make a Map out of a case class" in {
    case class A(foo: String, bar: Int)
    val a = A("aze", 10)
    caseClassToMap(a) should contain("foo" -> "aze", "bar" -> 10)
  }

  "mapToQueryString" should
    "make a query string out of a Map" in {
    val m = Map("foo" -> 1, "bar" -> "2")
    mapToQueryString(m) shouldBe "foo=1&bar=2"
  }

  "hideStringExceptLastN" should
    "hide the string except the last N characters" in {
    an [IllegalArgumentException] should be thrownBy hideStringExceptLastN("abc", -1)
    hideStringExceptLastN("abc", 0, "X") shouldBe "XXX"
    hideStringExceptLastN("abc", 0) shouldBe "***"
    hideStringExceptLastN("abc", 1) shouldBe "**c"
    hideStringExceptLastN("abc", 2) shouldBe "*bc"
    hideStringExceptLastN("abc", 3) shouldBe "abc"
    hideStringExceptLastN("abc", 4) shouldBe "***"
  }
}