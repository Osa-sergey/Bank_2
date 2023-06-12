package misis

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.typesafe.config.ConfigFactory
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import misis.kafka.FeeStreams
import misis.repository.FeeRepository
import misis.route.FeeRoute


object FeeApp extends App  {
    implicit val system: ActorSystem = ActorSystem("FeeApp")
    implicit val ec = system.dispatcher
    private val port = ConfigFactory.load().getInt("port")
    private val limit = ConfigFactory.load().getInt("fee.limit")
    private val percent = ConfigFactory.load().getInt("fee.percent")

    private val repository = new FeeRepository(limit, percent)
    new FeeStreams(repository)

    private val route = new FeeRoute()
    Http().newServerAt("0.0.0.0", port).bind(route.routes)
}
