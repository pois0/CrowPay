package jp.pois.crowpay.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.pois.crowpay.repos.BalanceRepository
import javax.inject.Inject

@HiltViewModel
class RepaymentDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    balanceRepository: BalanceRepository
): ViewModel() {
    private val repaymentId: Long = savedStateHandle.get<Long>(REPAYMENT_ID_SAVED_STATE_KEY)!!

    val repayment = balanceRepository.getRepayment(repaymentId)
    val balances = balanceRepository.getBalancesByRepayment(repaymentId)

    companion object {
        private const val REPAYMENT_ID_SAVED_STATE_KEY = "repaymentId"
    }
}
