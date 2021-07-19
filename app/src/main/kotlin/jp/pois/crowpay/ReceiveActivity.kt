package jp.pois.crowpay

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import dagger.hilt.android.AndroidEntryPoint
import jp.pois.crowpay.comm.ReceiverState
import jp.pois.crowpay.data.*
import jp.pois.crowpay.databinding.ActivityReceiveBinding
import jp.pois.crowpay.fragments.receive.CommunicatingFragmentDirections
import jp.pois.crowpay.fragments.receive.WaitingFragmentDirections
import jp.pois.crowpay.repos.entities.Balance
import jp.pois.crowpay.ui.confirmPermissions
import jp.pois.crowpay.utils.*
import jp.pois.crowpay.viewmodels.ReceiveViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf

@AndroidEntryPoint
class ReceiveActivity : AppCompatActivity() {
    private val connectionsClient by lazy { Nearby.getConnectionsClient(this) }

    private val viewModel by viewModels<ReceiveViewModel>()

    private val endpointId
        get() = viewModel.endpointId ?: throw IllegalStateException()

    private val navController
        get() = findNavController(R.id.fragment_container)

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        @OptIn(ExperimentalSerializationApi::class)
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            Log.d("ReceiveActivity:onConnectionInitiated", "${endpointId}, ${connectionInfo.endpointName}")
            lifecycleScope.launch {
                val identifier = try {
                    ProtoBuf.decodeFromByteArray<EndpointIdentifier>(connectionInfo.endpointInfo)
                } catch (e: Throwable) {
                    connectionsClient.rejectConnection(endpointId)
                    return@launch
                }

                if (viewModel.setUser(identifier)) {
                    connectionsClient.acceptConnection(endpointId, payloadCallback)
                    viewModel.endpointId = endpointId
                }
            }
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {}

        override fun onDisconnected(endpointId: String) {
            if (endpointId != viewModel.endpointId) return

            when (viewModel.state.get()) {
                null -> throw IllegalStateException()
                ReceiverState.FINISHED -> {}
                else -> showCommunicationErrorDialog()
            }
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            lifecycleScope.launch {
                if (payload.type != Payload.Type.BYTES) {
                    showCommunicationErrorDialog()
                    return@launch
                }

                val bytes = payload.asBytes()!!

                when (viewModel.state.get()) {
                    null -> throw IllegalStateException()
                    ReceiverState.WAITING -> {
                        val balance = runCatching { decodeProtoBuf<Balance>(bytes) }.getOrNull()
                        if (balance != null) {
                            onReceiveBalance(balance)
                            return@launch
                        }

                        val repayment = runCatching { decodeProtoBuf<RepaymentPayload>(bytes) }.getOrNull()
                        if (repayment != null) {
                            onReceiveRepayment(repayment)
                            return@launch
                        }

                        showCommunicationErrorDialog()
                    }
                    ReceiverState.ACCEPTED -> {
                        val insert = runCatching { decodeProtoBuf<InsertResult>(bytes) }.getOrNull()
                        if (insert == null) {
                            showCommunicationErrorDialog()
                            return@launch
                        }

                        if (insert.isSucceeded) {
                            when {
                                viewModel.balance != null -> {
                                    val succeeded = viewModel.runCatching { insertBalance() }.isSuccess

                                    sendInsertResult(succeeded)

                                    if (succeeded) {
                                        viewModel.state.set(ReceiverState.FINISHED)
                                        val action = CommunicatingFragmentDirections.actionViewCommunicatingFragmentToCompleteBalanceFragment(viewModel.balance!!, viewModel.user)
                                        navController.navigate(action)
                                    } else {
                                        showErrorOccurredDialog()
                                    }
                                }
                                viewModel.repayment != null -> {
                                    val succeeded = viewModel.runCatching { insertRepayment() }.isSuccess

                                    sendInsertResult(succeeded)

                                    if (succeeded) {
                                        viewModel.state.set(ReceiverState.FINISHED)
                                        val action = CommunicatingFragmentDirections.actionViewCommunicatingFragmentToCompleteRepaymentFragment(viewModel.repayment!!, viewModel.user)
                                        navController.navigate(action)
                                    }
                                }
                                else -> {
                                    throw IllegalStateException()
                                }
                            }
                        }
                    }
                    ReceiverState.VERIFICATION_FAILED -> {

                    }
                }
            }
        }

        override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) {}
    }


    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
        if (map.values.all { it }) {
            startAdvertising()
        } else {
            showFailedDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView<ActivityReceiveBinding>(this, R.layout.activity_receive)

        confirmPermissions(this) { hasPermissions, permissions ->
            if (!hasPermissions) {
                if (permissions == null) {
                    showFailedDialog()
                    return@confirmPermissions
                }
                permissionLauncher.launch(permissions)
            } else {
                startAdvertising()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        connectionsClient.stopAdvertising()
        connectionsClient.stopAllEndpoints()
    }

    fun rejectApplication() {
        send(endpointId, AcceptanceResult(false))
        viewModel.state.set(ReceiverState.FINISHED)
        finish()
    }

    fun sendApplicationAccepted() {
        send(endpointId, AcceptanceResult(true))
        viewModel.state.set(ReceiverState.ACCEPTED)
    }

    private inline fun <reified T> send(endpointId: String, content: T) = connectionsClient.send(endpointId, content)

    private fun startAdvertising() {
        connectionsClient.startAdvertising(endpointIdentifierBytes, serviceId, connectionLifecycleCallback, p2pAdvertiseOption)
    }

    private fun sendInconsistencyError() = send(viewModel.endpointId!!, InconsistencyError())

    private fun sendInsertResult(isSucceeded: Boolean) = send(viewModel.endpointId!!, InsertResult(isSucceeded))

    private fun onReceiveBalance(balance: Balance) {
        viewModel.balance = balance
        val action = WaitingFragmentDirections.actionViewWaitingFragmentToConfirmBalanceFragment(balance, viewModel.user)
        navController.navigate(action)
        viewModel.state.set(ReceiverState.RECEIVED)
    }

    private suspend fun onReceiveRepayment(repayment: RepaymentPayload) {
        val balances = viewModel.verifyRepayment(repayment)
        val user = viewModel.user
        if (balances.any { it.otherPartyId != user.id }) {
            sendInconsistencyError()
            viewModel.state.set(ReceiverState.VERIFICATION_FAILED)
            showInconsistencyErrorDialog()
            return
        }

        val rep = RepaymentWithBalances(repayment.repayment.copy(otherPartyId = user.id), balances)
        viewModel.repayment = rep
        val action = WaitingFragmentDirections.actionViewWaitingFragmentToConfirmRepaymentFragment(rep, user)
        navController.navigate(action)
        viewModel.state.set(ReceiverState.RECEIVED)
    }

    private fun showInconsistencyErrorDialog() {
        AlertDialog.Builder(this)
            .setMessage(R.string.inconsistency_error)
            .setCancelable(false)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                finish()
                dialog.dismiss()
            }
            .show()
    }
}
