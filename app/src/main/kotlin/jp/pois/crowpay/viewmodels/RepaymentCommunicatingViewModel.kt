package jp.pois.crowpay.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.pois.crowpay.data.RepaymentPayload
import jp.pois.crowpay.repos.BalanceRepository
import javax.inject.Inject

@HiltViewModel
class RepaymentCommunicatingViewModel @Inject constructor(
    private val balanceRepository: BalanceRepository
) : ViewModel() {
    suspend fun insertRepayment(repayment: RepaymentPayload) = balanceRepository.repayToOthers(repayment)
    suspend fun removeRepayment(id: Long) {
        // TODO
    }
}
