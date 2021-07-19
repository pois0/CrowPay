package jp.pois.crowpay.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.pois.crowpay.repos.UserRepository
import javax.inject.Inject

@HiltViewModel
class SetRepaymentViewModel @Inject constructor(
    userRepository: UserRepository
) : ViewModel() {
    val users = userRepository.getUsers().asLiveData()

}
