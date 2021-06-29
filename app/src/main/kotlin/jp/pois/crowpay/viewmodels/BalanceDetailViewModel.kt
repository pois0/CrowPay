package jp.pois.crowpay.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.pois.crowpay.repos.BalanceRepository
import javax.inject.Inject

@HiltViewModel
class BalanceDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    balanceRepository: BalanceRepository
) : ViewModel() {
    private val balanceId = savedStateHandle.get<Long>(BALANCE_ID_SAVED_STATE_KEY)!!

    val balance = balanceRepository.getBalance(balanceId).asLiveData()

    companion object {
        private const val BALANCE_ID_SAVED_STATE_KEY = "balanceId"
    }
}
