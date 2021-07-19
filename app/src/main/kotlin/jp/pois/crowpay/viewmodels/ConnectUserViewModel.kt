package jp.pois.crowpay.viewmodels

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.pois.crowpay.repos.UserRepository
import jp.pois.crowpay.repos.entities.User
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ConnectUserViewModel @Inject constructor(
    private val userRep: UserRepository
) : ViewModel() {
    val userList = ObservableArrayList<Data>()

    fun addUser(endpointId: String, uuid: UUID, name: String) {
        viewModelScope.launch {
            val user = userRep.putUser(uuid, name)
            userList.add(Data(endpointId, user))
        }
    }

    fun removeUser(endpointId: String) {
        for (i in userList.indices) {
            if (userList[i].endpointId == endpointId) {
                userList.removeAt(i)
                return
            }
        }
    }

    data class Data(
        val endpointId: String,
        val user: User
    )
}
