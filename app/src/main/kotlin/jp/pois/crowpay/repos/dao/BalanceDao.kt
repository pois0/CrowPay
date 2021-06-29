package jp.pois.crowpay.repos.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import jp.pois.crowpay.repos.entities.*
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface BalanceDao {
    @Query("""
        SELECT balances.*,
               users.id AS user_id, users.customized_name AS user_customized_name, users.uuid AS user_uuid, users.name AS user_name
        FROM balances
        INNER JOIN users ON balances.other_party_id = users.id 
    """)
    fun getBalances(): Flow<List<BalanceWithUser>>

    @Query("SELECT * FROM balances WHERE other_party_id = :otherPartyId")
    fun getBalances(otherPartyId: Long): Flow<List<Balance>>

    @Query("SELECT * FROM balances WHERE repaid_by = :repaymentId")
    fun getBalancesByRepayment(repaymentId: Long): Flow<List<Balance>>

    @Query("""
        SELECT balances.*,
               users.id AS user_id, users.customized_name AS user_customized_name, users.uuid AS user_uuid, users.name AS user_name,
               repayments.id AS repayment_id, repayments.other_party_id AS repayment_other_party_id, repayments.uuid AS repayment_uuid, repayments.amount AS repayment_amount, repayments.created_at AS repayment_created_at
        FROM balances
        INNER JOIN users ON balances.other_party_id = users.id
        LEFT OUTER JOIN repayments ON balances.repaid_by = repayments.id
        WHERE balances.id = :id
    """)
    fun getBalance(id: Long): Flow<BalanceDetail>

    @Query("SELECT SUM(amount) FROM balances WHERE repaid_by IS NULL AND other_party_id = :otherPartyId")
    fun getUnpaidBalanceSum(otherPartyId: Long): Flow<Int>

    @Query("""
        SELECT *,
               users.id AS user_id, users.customized_name AS user_customized_name, users.uuid AS user_uuid, users.name AS user_name
        FROM repayments
        INNER JOIN users ON repayments.other_party_id = users.id
    """)
    fun getRepayments(): Flow<List<RepaymentWithUser>>

    @Query("SELECT * FROM repayments WHERE other_party_id = :otherPartyId")
    fun getRepayments(otherPartyId: Long): Flow<List<Repayment>>

    @Query("""
        SELECT repayments.*,
               users.id AS user_id, users.customized_name AS user_customized_name, users.uuid AS user_uuid, users.name AS user_name
        FROM repayments
        INNER JOIN users ON repayments.other_party_id = users.id
        WHERE repayments.id = :id
    """)
    fun getRepayment(id: Long): Flow<RepaymentDetail>

    @Insert
    suspend fun insertBalance(balance: Balance): Long

    @Insert
    suspend fun insertRepayment(repayment: Repayment): Long

    @Query("UPDATE balances SET repaid_by = :repaymentId WHERE uuid IN (:balanceIds)")
    suspend fun repayBalances(repaymentId: Long, balanceIds: Array<UUID>)

    @Transaction
    suspend fun repay(repayment: Repayment, balanceIds: Array<UUID>): Long {
        val repaymentId = insertRepayment(repayment)
        repayBalances(repaymentId, balanceIds)
        return repaymentId
    }

    @Query("UPDATE balances SET remark = :remark WHERE id = :id")
    suspend fun updateRemark(id: Long, remark: String)

    @Query("DELETE FROM balances WHERE id = :id")
    suspend fun deleteBalance(id: Long): Int
}
