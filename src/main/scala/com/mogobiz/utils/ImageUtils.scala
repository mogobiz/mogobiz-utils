package com.mogobiz.utils

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.Files._
import java.nio.file.Paths.get

import com.mogobiz.utils.MimeTypeTools._
import com.mortennobel.imagescaling.{AdvancedResizeOp, ResampleOp}

object ImageUtils {

  def resizeImage(file:File, width:Int, height:Int):Option[File] = {
    toFormat(file) match {
      case Some(format) =>
        val out = new File(s"${file.getAbsolutePath}.${width}x$height.$format")
        if(!out.exists()){
          val src:BufferedImage = ImageIO.read(file)
          val originalWidth = src.getWidth
          val originalHeight = src.getHeight
          if(width == originalWidth && height == originalHeight){
            copy (get(file.getAbsolutePath), get(out.getAbsolutePath), REPLACE_EXISTING)
          }
          else{
            val resampleOp: ResampleOp = new ResampleOp(width, height)
            resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.VerySharp)
            ImageIO.write(resampleOp.filter(src, null), format, out)
          }
        }
        Some(out)
      case None => None
    }
  }

  def getFile(inputFile: File, size: Option[ImageSize], create: Boolean): File = {
    size match {
      case Some(s) =>
        val format = toFormat(detectMimeType(inputFile))
        val file: File = new File(s"${inputFile.getAbsolutePath}.${s.width}x${s.height}.$format")
        if (!file.exists() && create) {
          for (imageSize <- imageSizes.values){
            resizeImage(file, imageSize.width, imageSize.height)
          }
        }
        file
      case _ => inputFile
    }
  }

  val SMALL = "SMALL"

  val ICON = "ICON"

  val imageSizes = Map[String, ImageSize](ICON -> Icon, SMALL -> Small)

  trait ImageSize {
    def width: Int

    def height: Int
  }

  case object Icon extends ImageSize {
    val width = 32
    val height = 32
  }

  case object Small extends ImageSize {
    val width = 240
    val height = 240
  }

}
