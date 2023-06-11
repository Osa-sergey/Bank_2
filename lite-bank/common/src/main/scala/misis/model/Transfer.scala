package misis.model

import java.util.UUID

case class TransferRequest(from: Int, to: Int, amount: Int)
case class TransferStart(from: Int, to: Int, amount: Int, id: UUID = UUID.randomUUID()) extends Command


case class TransferStarted(to: Int, amount: Int, id: UUID, isSuccess: Boolean) extends Event
case class TransferFinished(id: UUID, isSuccess: Boolean) extends Event
