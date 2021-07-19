package jp.pois.crowpay.data

import jp.pois.crowpay.repos.entities.Balance
import jp.pois.crowpay.repos.entities.Repayment
import java.io.Serializable

data class RepaymentWithBalances(
    val repayment: Repayment,
    val balances: ArrayList<Balance>
): Serializable
