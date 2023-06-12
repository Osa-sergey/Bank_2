package misis.repository

import misis.model._


class AccountRepository(val startAccountId: Int, val endAccountId: Int) {

    private var accounts: List[Account] = List()

    def contains(accountId: Int): Boolean =
        if (accounts.indexWhere(_.id == accountId) == -1) false else true

    private def getBalance(accountId: Int): Int = {
        val idx = accounts.indexWhere(_.id == accountId)
        accounts(idx).amount
    }

    def printUpdateResult(res: AccountUpdated) =
        println(s"Account ${res.accountId} updated successful ${res.success}" +
            s" with value ${res.value}. " +
            s"Balance: ${getBalance(res.accountId)}")

    def updateAccount(accountId: Int, value: Int): AccountUpdated = {
        val posUpdate = AccountUpdated(accountId = accountId, value = value, success = true)
        val negUpdate = AccountUpdated(accountId = accountId, value = value, success = false)
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

    def createAccount(accountId: Int, balance: Int): AccountCreated =
        if (accounts.indexWhere(_.id == accountId) == -1) {
            val account = Account(accountId, balance)
            accounts = accounts :+ account
            println(s"Account with id:${accountId} created")
            AccountCreated(accountId, success = true, balance = balance)
        } else {
            println(s"Account with id:${accountId} exists")
            AccountCreated(accountId, success = false, balance = balance)
        }

}
