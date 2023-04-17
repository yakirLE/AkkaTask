import akka.actor.{Actor, ActorRef, Props}

import java.net.URL
import java.util.concurrent.atomic.AtomicInteger
import scala.io.Source

case class SiteMsg(siteUrl: String)
case class ImageSavedMsg(imageName: String)

class ImagesScraper extends Actor {
  private val imgRegex = "<img[^>]+src=\"([^\">]+\\.(\\w+))\"[^>]*>".r
  private var siteSender: Option[ActorRef] = None
  private var allFilesCounter: Int = 0
  private val handledFilesCounter = new AtomicInteger(0)

  override def receive = {
    case SiteMsg(siteUrl) => {
      siteSender = Some(sender())
      val imgUrls = scrapeImages(siteUrl)
      allFilesCounter = imgUrls.length
      imgUrls.foreach(u => context.actorOf(Props[ImageDownloader]) ! ImageUrlMsg(u))
    }
    case ImageSavedMsg(imageName) => {
      println(s"received image saved signal for $imageName")
      if(handledFilesCounter.incrementAndGet() == allFilesCounter) {
        println(s"last image $imageName")
        siteSender.map(_ ! allFilesCounter)
      }
    }
    case _ => println("Unsupported message in ImagesScraper")
  }

  private def scrapeImages(siteUrl: String): List[String] = {
    val htmlSource = Source.fromURL(new URL(siteUrl))
    val htmlContent = htmlSource.mkString
    imgRegex.findAllMatchIn(htmlContent).map(_.group(1)).toList
  }
}