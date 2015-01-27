package com.mogobiz.utils

import java.io.{InputStreamReader, BufferedReader}

/**
 *
 * Created by smanciot on 26/01/15.
 */
object ExecuteShellTools {

  def executeCommand(command:String):String = {
    val output:StringBuilder = new StringBuilder
    val p:Process = Runtime.getRuntime.exec(command)
    p.waitFor()
    val reader:BufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream))
    var line = reader.readLine()
    while(line != null){
      output.append(line + "\n")
      line = reader.readLine()
    }
    output.toString()
  }
}
