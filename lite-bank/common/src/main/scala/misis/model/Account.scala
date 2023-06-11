package misis.model

import java.time.Instant
import java.util.UUID

trait Command
trait Event

case class Account(id: Int, amount: Int = 0) {
    def update(value: Int) = this.copy(amount = amount + value)
}


case class AccountUpdate(accountId: Int, value: Int, category: Option[String]) extends Command
case class AccountCreate(accountId: Int) extends Command


case class AccountUpdated(
                             operationId: UUID = UUID.randomUUID(),
                             accountId: Int,
                             value: Int,
                             success: Boolean,
                             publishedAt: Option[Instant] = Some(Instant.now()),
                             category: Option[String]
                         ) extends Event

case class AccountCreated(
                             accountId: Int,
                             success: Boolean,
                             publishedAt: Option[Instant] = Some(Instant.now()),
                             operationId: UUID = UUID.randomUUID()
                         ) extends Event