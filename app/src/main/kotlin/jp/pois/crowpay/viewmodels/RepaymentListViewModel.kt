package jp.pois.crowpay.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.pois.crowpay.repos.BalanceRepository
import javax.inject.Inject

@HiltViewModel
class RepaymentListViewModel @Inject constructor(
    balanceRepository: BalanceRepository
) : ViewModel() {
    val repayments = balanceRepository.getRepayments().asLiveData()
}
