package misis

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.typesafe.config.ConfigFactory
import misis.kafka.CashBackStreams
import misis.repository.CashbackRepository
import misis.route.CashbackRoute
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._


object CashbackApp extends App  {
    implicit val system: ActorSystem = ActorSystem("CashbackApp")
    implicit val ec = system.dispatcher
    private val port = ConfigFactory.load().getInt("port")
    private val category = ConfigFactory.load().getString("cashback.category")
    private val percent = ConfigFactory.load().getInt("cashback.percent")
    private val rootAccId = ConfigFactory.load().getInt("rootId")

    private val repository = new CashbackRepository(category, percent)
    new CashBackStreams(repository, rootAccId)

    private val route = new CashbackRoute()
    Http().newServerAt("0.0.0.0", port).bind(route.routes)
}
