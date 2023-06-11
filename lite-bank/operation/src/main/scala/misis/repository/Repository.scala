package misis.repository

import io.circe.Encoder
import misis.TopicName
import misis.kafka.Streams
import misis.model._


class Repository(streams: Streams){

    def transfer(transfer: TransferStart)(implicit encoder: Encoder[TransferStart], topicName: TopicName[TransferStart]) = {
        if(transfer.amount > 0) streams.produceCommand(transfer)
    }
}
