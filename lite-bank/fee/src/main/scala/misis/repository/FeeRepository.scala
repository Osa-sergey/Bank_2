package misis.repository

import misis.model.TransferPrepare

import scala.collection.mutable

class FeeRepository(limit: Int, percent: Int) {
    private var limits: mutable.Map[Int, Int] = mutable.Map()

    def calcLimits(event: TransferPrepare): Int = {
        if(!limits.contains(event.from)) limits(event.from) = limit
        val limitBalance = limits(event.from)
        limits(event.from) = limits(event.from) - event.amount
        limitBalance
    }

    def calcFee(value: Int): Int = {
        value * percent / 100
    }

}
