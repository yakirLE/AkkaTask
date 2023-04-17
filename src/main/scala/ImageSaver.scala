import akka.actor.{Actor, ActorRef}

import java.io.{FileOutputStream, InputStream}

case class ImageContentMsg(imageName: String, content: InputStream, replyTo: ActorRef)

class ImageSaver extends Actor {
  override def receive = {
    case ImageContentMsg(fileName, inputStream, replyTo) => {
      println(s"saving $fileName")
      saveContentToFS(fileName, inputStream)
      println(s"finished $fileName")
      replyTo ! ImageSavedMsg(fileName)
    }
    case _ => println("Unsupported message in ImageSaver")
  }

  private def saveContentToFS(fileName: String, is: InputStream): Unit = {
    var fileOutputStream: FileOutputStream = null
    try {
      fileOutputStream = new FileOutputStream(fileName)
      val buffer = new Array[Byte](1024)
      var bytesRead = is.read(buffer)

      while (bytesRead != -1) {
        fileOutputStream.write(buffer, 0, bytesRead)
        bytesRead = is.read(buffer)
      }
    } catch {
      case ex: Exception =>
        println(s"failed to save $fileName content to FileSystem with error: ${ex.getMessage}")
    } finally {
      if(is != null) is.close()
      if(fileOutputStream != null) fileOutputStream.close()
    }
  }
}