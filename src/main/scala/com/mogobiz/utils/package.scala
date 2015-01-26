package com.mogobiz

/**
 *
 * Created by smanciot on 26/01/15.
 */
package object utils {

  implicit class Regex(sc: StringContext) {
    def r = new util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
  }

}
