package misis.repository

import io.circe.Encoder
import misis.TopicName
import misis.kafka.OperationStreams
import misis.model._



class OperationRepository(streams: OperationStreams){

    def transfer(transfer: TransferPrepare)(implicit encoder: Encoder[TransferPrepare], topicName: TopicName[TransferPrepare]) = {
        if(transfer.amount > 0) streams.produceCommand(transfer)
    }
}
