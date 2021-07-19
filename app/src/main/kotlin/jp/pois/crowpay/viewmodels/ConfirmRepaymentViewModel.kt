package jp.pois.crowpay.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.pois.crowpay.repos.UserRepository
import javax.inject.Inject

@HiltViewModel
class ConfirmRepaymentViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
}
