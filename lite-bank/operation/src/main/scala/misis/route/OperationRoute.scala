package misis.route

import akka.http.scaladsl.server.Directives._
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import misis.TopicName
import misis.kafka.OperationStreams
import misis.model._
import misis.repository.OperationRepository

import scala.concurrent.ExecutionContext


class OperationRoute(streams: OperationStreams, repository: OperationRepository)(implicit ec: ExecutionContext) extends FailFastCirceSupport {

    implicit val topicNameAccountUpdate: TopicName[AccountUpdate] = streams.simpleTopicName[AccountUpdate]
    implicit val topicNameAccountCreate: TopicName[AccountCreate] = streams.simpleTopicName[AccountCreate]
    implicit val topicNameTransferPrepare: TopicName[TransferPrepare] = streams.simpleTopicName[TransferPrepare]

    def routes =
        (path("healthcheck") & get) {
            complete("ok")
        } ~
            (path("accrue" / IntNumber / IntNumber) { (accountId, amount) =>
                val command = AccountUpdate(accountId, amount)
                streams.produceCommand(command)
                complete(command)
            }) ~
            (path("withdraw" / IntNumber / IntNumber) { (accountId, amount) =>
                val command = AccountUpdate(accountId, -amount)
                streams.produceCommand(command)
                complete(command)
            }) ~
            (path("create" / IntNumber ) { (accountId) =>
                val command = AccountCreate(accountId)
                streams.produceCommand(command)
                complete(command)
            }) ~
            (path("transfer") & post & entity(as[TransferRequest])) { transfer =>
                repository.transfer(TransferPrepare(transfer.from, transfer.to, transfer.amount, transfer.category))
                complete(transfer)
            }
}


