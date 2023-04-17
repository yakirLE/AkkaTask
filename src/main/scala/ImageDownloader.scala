import akka.actor.{Actor, Props}

import java.io.InputStream
import java.net.URL

case class ImageUrlMsg(imageUrl: String)

class ImageDownloader extends Actor {
  override def receive = {
    case ImageUrlMsg(imageUrl) => {
      println(s"downloading $imageUrl")
      val inputStream = getImageContentFromUrl(imageUrl)
      if(inputStream.isDefined) {
        val fileName = "images/" + getFilenameFromUrl(imageUrl)
        context.actorOf(Props[ImageSaver]) ! ImageContentMsg(fileName, inputStream.get, sender)
      }
    }
    case _ => println("Unsupported message in ImageDownloader")
  }

  private def getImageContentFromUrl(imageUrl: String): Option[InputStream] = {
    try {
      val fullImgUrl = new URL(imageUrl)
      val imageConnection = fullImgUrl.openConnection
      Some(imageConnection.getInputStream)
    } catch {
      case ex: Exception =>
        println(s"failed to get content from image URL $imageUrl with error: ${ex.getMessage}")
        None
    }
  }

  private def getFilenameFromUrl(url: String): String = {
    url.substring(url.lastIndexOf("/") + 1).replaceAll("(?i)%[2-9a-f][0-9a-f]", "_")
  }
}