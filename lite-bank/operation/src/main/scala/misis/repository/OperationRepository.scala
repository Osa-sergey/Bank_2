package misis.repository

import io.circe.Encoder
import misis.TopicName
import misis.kafka.OperationStreams
import misis.model._


class OperationRepository(streams: OperationStreams){

    def transfer(transfer: TransferStart)(implicit encoder: Encoder[TransferStart], topicName: TopicName[TransferStart]) = {
        if(transfer.amount > 0) streams.produceCommand(transfer)
    }
}
