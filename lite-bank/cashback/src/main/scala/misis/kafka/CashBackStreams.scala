package misis.kafka

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink}
import io.circe.generic.auto._
import io.circe.syntax._
import misis.WithKafka
import misis.model._
import misis.repository.{CashbackRepository}

import scala.concurrent.{ExecutionContext, Future}

class CashBackStreams(repository: CashbackRepository, rootAccId: Int)(implicit val system: ActorSystem, executionContext: ExecutionContext)
    extends WithKafka {
    override def group: String = "cashback"

    kafkaSource[TransferFinished]
        .mapAsync(1) { event =>
            val cashback = repository.calcCashback(event)
            if(cashback > 0) produceCommand(TransferStart(rootAccId, event.from, cashback, 0, Some("cashback"), event.id))
            Future.successful()
        }
        .to(Sink.ignore)
        .run()
}
