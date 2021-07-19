package jp.pois.crowpay.fragments.balance

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.nearby.connection.*
import dagger.hilt.android.AndroidEntryPoint
import jp.pois.crowpay.NewBalanceActivity
import jp.pois.crowpay.R
import jp.pois.crowpay.comm.SenderState
import jp.pois.crowpay.data.AcceptanceResult
import jp.pois.crowpay.data.InsertResult
import jp.pois.crowpay.utils.decodeProtoBuf
import jp.pois.crowpay.utils.showCommunicationErrorDialog
import jp.pois.crowpay.utils.showErrorOccurredDialog
import jp.pois.crowpay.viewmodels.BalanceCommunicatingViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CommunicatingFragment : Fragment() {
    private val viewModel by viewModels<BalanceCommunicatingViewModel>()
    private val activity: NewBalanceActivity
        get() = getActivity() as NewBalanceActivity

    private val args by navArgs<CommunicatingFragmentArgs>()
    private var state = SenderState.SENT

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (endpointId != args.endpointId) {
                activity.run {
                    disconnect(endpointId)
                    disconnect(args.endpointId)
                    showErrorOccurredDialog()
                }
                return
            }

            lifecycleScope.launch {
                val bytes = payload.asBytes()

                if (bytes == null) {
                    activity.showCommunicationErrorDialog()
                    return@launch
                }

                when (state) {
                    SenderState.SENT -> {
                        val result = decodeProtoBuf<AcceptanceResult>(bytes)
                        if (result.isAccepted) {
                            state = SenderState.ACCEPTED
                            try {
                                val balance = viewModel.insertBalance(args.balance, args.user.id)
                                viewModel.balanceId = balance.id
                            } catch (e: Throwable) {
                                activity.disconnect(endpointId)
                                activity.showErrorOccurredDialog()
                                return@launch
                            }

                            activity.sendInserted(endpointId).addOnSuccessListener {
                                state = SenderState.INSERTED
                            }.addOnFailureListener {
                                lifecycleScope.launch {
                                    activity.disconnect(endpointId)
                                    rollback()
                                }
                            }
                        } else {
                            state = SenderState.FINISHED
                            showRejectedDialog()
                        }
                    }
                    SenderState.INSERTED -> {
                        val result = decodeProtoBuf<InsertResult>(bytes)
                        state = SenderState.FINISHED
                        activity.disconnect(endpointId)
                        if (result.isSucceeded) {
                            val action = CommunicatingFragmentDirections.actionViewCommunicatingFragmentToSentBalanceFragment(args.balance, args.user)
                            findNavController().navigate(action)
                        } else {
                            rollback()
                        }
                    }
                    SenderState.ACCEPTED, SenderState.FINISHED -> {}
                }
            }
        }

        override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_communicating, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity.acceptUser(args.endpointId, object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
                activity.acceptConnection(endpointId, payloadCallback)
            }

            override fun onConnectionResult(endpointId: String, connectionResolution: ConnectionResolution) {
                if (connectionResolution.status.statusCode == ConnectionsStatusCodes.STATUS_OK) {
                    viewModel.endpointId = endpointId
                    activity.sendBalance(endpointId, args.balance)
                } else {
                    activity.showCommunicationErrorDialog()
                }
            }

            override fun onDisconnected(p0: String) {
                when (state) {
                    SenderState.FINISHED -> {}

                }
            }
        })
    }

    private fun showRejectedDialog() {
        AlertDialog.Builder(activity)
            .setMessage(R.string.rejected_application)
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                activity.finish()
                dialog.dismiss()
            }
            .show()
    }

    private suspend fun rollback() {
        if (viewModel.balanceId >= 0) {
            viewModel.removeBalance(viewModel.balanceId)
        }

        activity.showErrorOccurredDialog()
    }
}
