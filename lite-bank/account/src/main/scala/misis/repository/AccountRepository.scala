package misis.repository

import misis.model._


class AccountRepository(val startAccountId: Int, val endAccountId: Int) {

    private var accounts: List[Account] = List()

    def contains(accountId: Int): Boolean =
        if (accounts.indexWhere(_.accountId == accountId) == -1) false else true

    private def getBalance(accountId: Int): Int = {
        val idx = accounts.indexWhere(_.accountId == accountId)
        accounts(idx).balance
    }

    def printUpdateResult(res: AccountUpdated): Unit =
        println(s"Account ${res.accountId} updated successful ${res.success}" +
            s" with value ${res.amount}. " +
            s"Balance: ${getBalance(res.accountId)}")

    def updateAccount(accountId: Int, amount: Int): AccountUpdated = {
        val posUpdate = AccountUpdated(accountId = accountId, amount = amount, success = true)
        val negUpdate = AccountUpdated(accountId = accountId, amount = amount, success = false)
        val idx = accounts.indexWhere(_.accountId == accountId)
        val accountBefore = accounts(idx)
        if (accountBefore.balance + amount >= 0) {
            val resAccount = accountBefore.update(amount)
            accounts = accounts.map(account => if (account.accountId == resAccount.accountId) resAccount else account)
            posUpdate
        } else {
            negUpdate
        }
    }

    def createAccount(accountId: Int, balance: Int): AccountCreated =
        if (accounts.indexWhere(_.accountId == accountId) == -1) {
            val account = Account(accountId, balance)
            accounts = accounts :+ account
            println(s"Account with id:${accountId} created")
            AccountCreated(accountId, success = true, balance = balance)
        } else {
            println(s"Account with id:${accountId} exists")
            AccountCreated(accountId, success = false, balance = balance)
        }

}
