package misis.model

import java.time.Instant
import java.util.UUID

trait Command
trait Event

case class Account(id: Int, amount: Int = 0) {
    def update(value: Int) = this.copy(amount = amount + value)
}


case class AccountUpdate(accountId: Int, value: Int) extends Command
case class AccountCreate(accountId: Int, balance: Int = 0) extends Command


case class AccountUpdated(
                             operationId: UUID = UUID.randomUUID(),
                             accountId: Int,
                             value: Int,
                             success: Boolean,
                             publishedAt: Option[Instant] = Some(Instant.now())
                         ) extends Event

case class AccountCreated(
                             accountId: Int,
                             balance: Int,
                             success: Boolean,
                             publishedAt: Option[Instant] = Some(Instant.now()),
                             operationId: UUID = UUID.randomUUID()
                         ) extends Event