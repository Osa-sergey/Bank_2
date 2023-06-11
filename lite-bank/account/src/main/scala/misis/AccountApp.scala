package misis

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.typesafe.config.ConfigFactory
import misis.kafka.{AccountStreams, TransferStreams}
import misis.repository.AccountRepository
import misis.route.AccountRoute
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._


object AccountApp extends App  {
    implicit val system: ActorSystem = ActorSystem("AccountApp")
    implicit val ec = system.dispatcher
    private val port = ConfigFactory.load().getInt("port")
    private val startAccountId = ConfigFactory.load().getInt("account.startAccountId")
    private val endAccountId = ConfigFactory.load().getInt("account.endAccountId")


    private val repository = new AccountRepository(startAccountId, endAccountId)
    new AccountStreams(repository)
    new TransferStreams(repository)

    private val route = new AccountRoute()
    Http().newServerAt("0.0.0.0", port).bind(route.routes)
}
