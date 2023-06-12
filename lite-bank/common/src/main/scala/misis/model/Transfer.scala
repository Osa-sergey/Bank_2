package misis.model

import java.util.UUID

case class TransferRequest(from: Int, to: Int, amount: Int, category: Option[String])
case class TransferStart(from: Int, to: Int, amount: Int, category: Option[String], id: UUID = UUID.randomUUID()) extends Command


case class TransferStarted(from: Int, to: Int, amount: Int, category: Option[String], id: UUID, isSuccess: Boolean) extends Event
case class TransferFinished(from: Int, to: Int, amount: Int, category: Option[String], id: UUID, isSuccess: Boolean) extends Event
