package jp.pois.crowpay.viewmodels

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.pois.crowpay.comm.ReceiverState
import jp.pois.crowpay.data.EndpointIdentifier
import jp.pois.crowpay.data.RepaymentPayload
import jp.pois.crowpay.data.RepaymentWithBalances
import jp.pois.crowpay.repos.BalanceRepository
import jp.pois.crowpay.repos.UserRepository
import jp.pois.crowpay.repos.entities.Balance
import jp.pois.crowpay.repos.entities.Repayment
import jp.pois.crowpay.repos.entities.User
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class ReceiveViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val balanceRepo: BalanceRepository
) : ViewModel() {
    private val mutex = Mutex()
    val state = ObservableField(ReceiverState.WAITING)
    var endpointId: String? = null
    var balance: Balance? = null
    var repayment: RepaymentWithBalances? = null
    val user: User
        get() = _user!!

    private var _user: User? = null

    suspend fun setUser(identifier: EndpointIdentifier): Boolean {
        if (_user != null) return false
        mutex.withLock {
            if (_user != null) return@setUser false
        }
        _user = userRepo.putUser(identifier.uuid, identifier.name)
        return true
    }

    suspend fun insertBalance() = balanceRepo.insertLendingBalance(balance!!, user.id)

    suspend fun insertRepayment() = balanceRepo.repayFromOthers(repayment!!)

    suspend fun verifyRepayment(repayment: RepaymentPayload): ArrayList<Balance> =
        ArrayList(repayment.balances.map { balanceRepo.getBalance(it) })
}
