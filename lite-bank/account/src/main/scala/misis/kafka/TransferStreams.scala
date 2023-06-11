package misis.kafka

import akka.actor.ActorSystem
import akka.stream.scaladsl.Sink
import misis.WithKafka
import io.circe.generic.auto._
import misis.model.{TransferFinished, TransferStart, TransferStarted}
import misis.repository.AccountRepository

import scala.concurrent.{ExecutionContext, Future}

class TransferStreams(repository: AccountRepository)(implicit val system: ActorSystem, executionContext: ExecutionContext)
    extends WithKafka {
    def group = s"account-${repository.startAccountId}"

    kafkaSource[TransferStart]
        .filter(command => repository.contains(command.from))
        .mapAsync(1) { command =>
            Future.successful(repository.transferWithdraw(command))
        }
        .to(kafkaSink)
        .run()

    kafkaSource[TransferStarted]
        .filter(event => repository.contains(event.to))
        .mapAsync(1) { command =>
            Future.successful(repository.transferAccrue(command))
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
