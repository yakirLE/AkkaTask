import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.global
import scala.language.postfixOps

object main extends App {
  implicit val ec = global
  implicit val timeout = Timeout(10 seconds)
  val system = ActorSystem("SiteImages")
  val actor = system.actorOf(Props[ImagesScraper])
  val siteUrl = "https://salt.security"
  val future = actor ? SiteMsg(siteUrl)
  future.map{totalImages =>
    println(s"finished saving $totalImages images")
    system.shutdown
  }
}
