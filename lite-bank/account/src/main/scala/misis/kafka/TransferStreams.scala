package misis.kafka

import akka.actor.ActorSystem
import akka.stream.scaladsl.Sink
import misis.WithKafka
import io.circe.generic.auto._
import misis.model.{AccountUpdated, TransferFinished, TransferStart, TransferStarted}
import misis.repository.AccountRepository

import scala.concurrent.{ExecutionContext, Future}

class TransferStreams(repository: AccountRepository)(implicit val system: ActorSystem, executionContext: ExecutionContext)
    extends WithKafka {
    def group = s"account-${repository.startAccountId}"

    kafkaSource[TransferStart]
        .filter(command => repository.contains(command.from))
        .mapAsync(1) { command =>
            val withdraw = repository.updateAccount(command.from, -(command.amount + command.fee))
            produceCommand(withdraw)
            Future.successful(
                TransferStarted(command.from, command.to, command.amount, command.category, command.id, withdraw.success)
            )
        }
        .to(kafkaSink)
        .run()

    kafkaSource[TransferStarted]
        .filter(event => repository.contains(event.to))
        .mapAsync(1) { event =>
            if (event.isSuccess) {
                val accrue = repository.updateAccount(event.to, event.amount)
                produceCommand(accrue)
                Future.successful(
                    TransferFinished(event.from, event.to, event.amount, event.category, event.id, accrue.success)
                )
            } else {
                Future.successful(
                    TransferFinished(event.from, event.to, event.amount, event.category, event.id, event.isSuccess)
                )
            }
        }
        .to(kafkaSink)
        .run()

    kafkaSource[TransferFinished]
        .map { event =>
            println(s"Transfer with ID: ${event.id} finished successfully ${event.isSuccess}")
            event
        }
        .to(Sink.ignore)
        .run()
}
