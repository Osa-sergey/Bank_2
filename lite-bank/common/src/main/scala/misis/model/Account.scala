package misis.model

import java.time.Instant
import java.util.UUID

trait Command
trait Event

case class Account(accountId: Int, balance: Int = 0) {
    def update(value: Int) = this.copy(balance = balance + value)
}


case class AccountUpdate(accountId: Int, amount: Int) extends Command
case class AccountCreate(accountId: Int, balance: Int = 0) extends Command


case class AccountUpdated(
                             accountId: Int,
                             amount: Int,
                             success: Boolean,
                             publishedAt: Option[Instant] = Some(Instant.now()),
                             operationId: UUID = UUID.randomUUID()
                         ) extends Event

case class AccountCreated(
                             accountId: Int,
                             balance: Int,
                             success: Boolean,
                             publishedAt: Option[Instant] = Some(Instant.now()),
                             operationId: UUID = UUID.randomUUID()
                         ) extends Event