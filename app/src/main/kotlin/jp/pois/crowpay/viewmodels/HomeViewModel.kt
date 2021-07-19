package jp.pois.crowpay.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.pois.crowpay.repos.BalanceRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    balanceRepository: BalanceRepository
) : ViewModel() {
    val sum = balanceRepository.getUnpaidBalanceSum()
}
