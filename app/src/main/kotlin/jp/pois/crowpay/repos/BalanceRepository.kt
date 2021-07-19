package jp.pois.crowpay.repos

import jp.pois.crowpay.data.RepaymentPayload
import jp.pois.crowpay.data.RepaymentWithBalances
import jp.pois.crowpay.repos.dao.BalanceDao
import jp.pois.crowpay.repos.entities.Balance
import jp.pois.crowpay.repos.entities.Repayment
import jp.pois.crowpay.utils.Direction
import java.time.LocalDate
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class BalanceRepository @Inject constructor(
    private val dao: BalanceDao
) {
    fun getBalances() = dao.getBalances()

    fun getBalances(otherPartyId: Long) = dao.getBalances(otherPartyId)

    fun getBalancesByRepayment(repaymentId: Long) = dao.getBalancesByRepayment(repaymentId)

    fun getBalance(id: Long) = dao.getBalance(id)

    fun getUnrepaidBalances(otherPartyId: Long) = dao.getUnrepaidBalances(otherPartyId)

    suspend fun getBalance(uuid: UUID) = dao.getBalance(uuid)

    fun getUnpaidBalanceSum() = dao.getUnpaidBalanceSum()

    fun getUnpaidBalanceSum(otherPartyId: Long) = dao.getUnpaidBalanceSum(otherPartyId)

    fun getRepayments() = dao.getRepayments()

    fun getRepayments(otherPartyId: Long) = dao.getRepayments(otherPartyId)

    fun getRepayment(id: Long) = dao.getRepayment(id)

    suspend fun getRepayment(uuid: UUID) = dao.getRepayment(uuid)

    private suspend fun insertBalance(balance: Balance): Balance {
        val balanceId = dao.insertBalance(balance)
        return balance.copy(id = balanceId)
    }

    suspend fun insertBorrowingBalance(balance: Balance, otherPartyId: Long): Balance {
        return insertBalance(balance.copy(otherPartyId = otherPartyId))
    }

    suspend fun insertLendingBalance(balance: Balance, otherPartyId: Long): Balance {
        return insertBalance(balance.copy(otherPartyId = otherPartyId, amount = -abs(balance.amount)))
    }

    suspend fun repayToOthers(repayment: RepaymentPayload): Repayment {
        val repaymentId = dao.repay(repayment.repayment, balanceIds = repayment.balances)
        return repayment.repayment.copy(id = repaymentId)
    }

    suspend fun repayFromOthers(repayment: RepaymentWithBalances): Repayment {
        val repaymentId = dao.repay(repayment.repayment.copy(amount = -repayment.repayment.amount), repayment.balances.map { it.uuid }.toTypedArray())
        return repayment.repayment.copy(id = repaymentId)
    }

    suspend fun updateRemark(id: Long, remark: String) = dao.updateRemark(id, remark)

    suspend fun deleteBalance(id: Long) = dao.deleteBalance(id)
}
