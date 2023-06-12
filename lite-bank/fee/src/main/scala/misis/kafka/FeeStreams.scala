package misis.kafka

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.{Sink, Source}
import io.circe.generic.auto._
import io.circe.syntax._
import misis.WithKafka
import misis.model._
import misis.model.{AccountUpdate, AccountUpdated}
import misis.repository.FeeRepository
import org.apache.kafka.clients.producer.ProducerRecord

import scala.concurrent.{ExecutionContext, Future}

class FeeStreams(repository: FeeRepository)(implicit val system: ActorSystem, executionContext: ExecutionContext)
    extends WithKafka {
    override def group: String = "fee"

    kafkaSource[TransferPrepare]
        .mapAsync(1) { event =>
            val limitBalance = repository.calcLimits(event)
            val taxableAmount =
                if(limitBalance < 0) event.amount
                else if(limitBalance - event.amount < 0) event.amount - limitBalance
                else 0
            Future.successful (
                produceCommand (
                    TransferStart(
                        event.from,
                        event.to,
                        event.amount,
                        repository.calcFee(taxableAmount),
                        event.category,
                        event.id)
                )
            )
        }
        .to(Sink.ignore)
        .run()
}
