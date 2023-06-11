package misis.repository

import misis.model._


class AccountRepository(val startAccountId: Int, val endAccountId: Int) {

    private var accounts: List[Account] = List()

    def contains(accountId: Int): Boolean =
        if (accounts.indexWhere(_.id == accountId) == -1) false else true

    def getBalance(accountId: Int): Int = {
        val idx = accounts.indexWhere(_.id == accountId)
        accounts(idx).amount
    }

    def transferWithdraw(command: TransferStart): TransferStarted = {
        val withdraw: AccountUpdated = updateAccount(command.from, -command.amount, Some("transfer_transaction"))
        printUpdateResult(withdraw)
        TransferStarted(command.to, command.amount, command.id, withdraw.success)
    }

    def transferAccrue(event: TransferStarted): TransferFinished = {
        if(event.isSuccess){
            val accrue: AccountUpdated = updateAccount(event.to, event.amount, Some("transfer_transaction"))
            printUpdateResult(accrue)
            TransferFinished(event.id, isSuccess = accrue.success)
        } else {
            TransferFinished(event.id, isSuccess = event.isSuccess)
        }
    }

    def printUpdateResult(res: AccountUpdated) =
        println(s"Account ${res.accountId} updated successful ${res.success}" +
            s" with value ${res.value} category: ${res.category.getOrElse("")}. " +
            s"Balance: ${getBalance(res.accountId)}")

    def updateAccount(accountId: Int, value: Int, category: Option[String] = None): AccountUpdated = {
        val posUpdate = AccountUpdated(accountId = accountId, value = value, success = true, category = category)
        val negUpdate = AccountUpdated(accountId = accountId, value = value, success = false, category = category)
        val idx = accounts.indexWhere(_.id == accountId)
        val accountBefore = accounts(idx)
        if (accountBefore.amount + value >= 0) {
            val resAccount = accountBefore.update(value)
            accounts = accounts.map(account => if (account.id == resAccount.id) resAccount else account)
            posUpdate
        } else {
            negUpdate
        }
    }

    def createAccount(accountId: Int): AccountCreated =
        if (accounts.indexWhere(_.id == accountId) == -1) {
            val account = Account(accountId)
            accounts = accounts :+ account
            println(s"Account with id:${accountId} created")
            AccountCreated(accountId, success = true)
        } else {
            println(s"Account with id:${accountId} exists")
            AccountCreated(accountId, success = false)
        }

}
