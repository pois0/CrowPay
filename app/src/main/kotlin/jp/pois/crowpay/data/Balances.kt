package jp.pois.crowpay.data

import jp.pois.crowpay.repos.entities.Balance
import java.io.Serializable

data class Balances(val balances: ArrayList<Balance>) : Serializable

val Balances.totalAmount: Int
    get() = balances.sumOf { it.amount }
