package jp.pois.crowpay.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.pois.crowpay.repos.BalanceRepository
import jp.pois.crowpay.repos.UserRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val balanceRepository: BalanceRepository
) : ViewModel() {
    private val userId: Long = savedStateHandle[USER_ID_SAVED_STATE_KEY]!!
    val user = userRepository.getUser(userId).asLiveData()
    val balances = balanceRepository.getBalances(userId).asLiveData()
    val repayments = balanceRepository.getRepayments(userId).asLiveData()

    fun updateCustomizedName(newName: String) {
        viewModelScope.launch {
            userRepository.updateCustomizedName(userId, newName)
        }
    }

    fun updateRemark(newRemark: String) {
        viewModelScope.launch {
            balanceRepository.updateRemark(userId, newRemark)
        }
    }

    companion object {
        private const val USER_ID_SAVED_STATE_KEY = "userId"
    }
}
