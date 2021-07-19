package jp.pois.crowpay.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.pois.crowpay.repos.BalanceRepository
import jp.pois.crowpay.repos.entities.Balance
import javax.inject.Inject

@HiltViewModel
class SelectBalancesViewModel @Inject constructor(
    private val repository: BalanceRepository
) : ViewModel() {
    val checkedBalances = ArrayList<Balance>()

    fun getBalances(otherPartyId: Long) = repository.getUnrepaidBalances(otherPartyId).asLiveData()
}
