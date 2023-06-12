package misis.repository

import misis.model.TransferFinished

import scala.concurrent.Future

class CashbackRepository(category: String, percent: Int) {
    def calcCashback(event: TransferFinished): Int =
        if(event.isSuccess && event.category.getOrElse("") == category) event.amount * percent / 100 else 0

}
