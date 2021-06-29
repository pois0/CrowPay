package jp.pois.crowpay.repos

import jp.pois.crowpay.repos.dao.BalanceDao
import jp.pois.crowpay.repos.entities.Balance
import jp.pois.crowpay.repos.entities.Repayment
import jp.pois.crowpay.utils.Direction
import java.time.LocalDate
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BalanceRepository @Inject constructor(
    private val dao: BalanceDao
) {
    fun getBalances() = dao.getBalances()

    fun getBalances(otherPartyId: Long) = dao.getBalances(otherPartyId)

    fun getBalancesByRepayment(repaymentId: Long) = dao.getBalancesByRepayment(repaymentId)

    fun getBalance(id: Long) = dao.getBalance(id)

    fun getRepayments() = dao.getRepayments()

    fun getRepayments(otherPartyId: Long) = dao.getRepayments(otherPartyId)

    fun getRepayment(id: Long) = dao.getRepayment(id)

    suspend fun createBorrowingBalance(
        otherPartyId: Long,
        amount: Int,
        deadline: LocalDate? = null,
        remark: String = ""
    ): Balance {
        val balance = Balance(
            otherPartyId = otherPartyId,
            amount = amount,
            deadline = deadline,
            remark = remark
        )
        val balanceId = dao.insertBalance(balance)
        return balance.copy(id = balanceId)
    }

    suspend fun createLendingBalance(
        balance: Balance,
        otherPartyId: Long
    ): Long = dao.insertBalance(balance.copy(otherPartyId = otherPartyId, amount = -balance.amount))

    suspend fun repayToOthers(otherPartyId: Long, amount: Int, balanceIds: Array<UUID>): Repayment {
        val repayment = Repayment(
            otherPartyId = otherPartyId,
            amount = amount
        )
        val repaymentId = dao.repay(repayment, balanceIds = balanceIds)
        return repayment.copy(id = repaymentId)
    }

    suspend fun repaidFromOthers(repayment: Repayment, otherPartyId: Long, balanceIds: Array<UUID>): Long
        = dao.repay(
            repayment.copy(otherPartyId = otherPartyId, amount = -repayment.amount),
            balanceIds
        )

    suspend fun updateRemark(id: Long, remark: String) = dao.updateRemark(id, remark)

    suspend fun deleteBalance(id: Long) = dao.deleteBalance(id)
}
