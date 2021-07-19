package jp.pois.crowpay.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.pois.crowpay.comm.SenderState
import jp.pois.crowpay.repos.BalanceRepository
import jp.pois.crowpay.repos.entities.Balance
import javax.inject.Inject

@HiltViewModel
class BalanceCommunicatingViewModel @Inject constructor(
    private val balanceRepo: BalanceRepository
) : ViewModel() {
    var endpointId: String? = null
    var balanceId = -1L

    suspend fun insertBalance(balance: Balance, otherPartyId: Long): Balance {
        return balanceRepo.insertBorrowingBalance(balance, otherPartyId)
    }

    suspend fun removeBalance(balanceId: Long) {
        balanceRepo.deleteBalance(balanceId)
    }
}
