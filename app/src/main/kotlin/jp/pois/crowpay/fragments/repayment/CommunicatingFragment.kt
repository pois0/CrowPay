package jp.pois.crowpay.fragments.repayment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.nearby.connection.*
import dagger.hilt.android.AndroidEntryPoint
import jp.pois.crowpay.NewRepaymentActivity
import jp.pois.crowpay.R
import jp.pois.crowpay.comm.SenderState
import jp.pois.crowpay.data.AcceptanceResult
import jp.pois.crowpay.data.InsertResult
import jp.pois.crowpay.data.RepaymentPayload
import jp.pois.crowpay.data.totalAmount
import jp.pois.crowpay.repos.entities.Repayment
import jp.pois.crowpay.ui.confirmPermissions
import jp.pois.crowpay.utils.decodeProtoBuf
import jp.pois.crowpay.utils.endpointIdentifier
import jp.pois.crowpay.utils.showCommunicationErrorDialog
import jp.pois.crowpay.utils.showErrorOccurredDialog
import jp.pois.crowpay.viewmodels.RepaymentCommunicatingViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CommunicatingFragment : Fragment() {
    private val activity
        get() = getActivity() as NewRepaymentActivity

    private val args by navArgs<CommunicatingFragmentArgs>()
    private val viewModel by viewModels<RepaymentCommunicatingViewModel>()
    private var state = SenderState.CONNECTED
    private lateinit var repaymentPayload: RepaymentPayload
    private var repaymentId: Long = 0
    private lateinit var endpointId: String

    private val discoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            val (uuid, _) = info.endpointIdentifier

            Log.d("CommunicationFragment", "endpoint found: expected_uuid: ${args.user.uuid} / $uuid")

            if (args.user.uuid == uuid) {
                activity.acceptUser(endpointId, connectionLifecycleCallback)
            }
        }

        override fun onEndpointLost(p0: String) {}
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            activity.acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(endpointId: String, connectionResolution: ConnectionResolution) {
            if (connectionResolution.status.statusCode == ConnectionsStatusCodes.STATUS_OK) {
                this@CommunicatingFragment.endpointId = endpointId

                val payload = RepaymentPayload(
                    Repayment(
                        otherPartyId = args.user.id,
                        amount = args.balances.totalAmount
                    ),
                    args.balances.balances.map { it.uuid }.toTypedArray()
                )

                repaymentPayload = payload

                activity.sendRepayment(endpointId, payload)
                    .addOnSuccessListener {
                        state = SenderState.SENT
                    }.addOnFailureListener {
                        activity.showCommunicationErrorDialog()
                    }
            } else {
                activity.showCommunicationErrorDialog()
            }
        }

        override fun onDisconnected(endpointId: String) {
            when (state) {
                SenderState.FINISHED -> {}
                SenderState.CONNECTED, SenderState.SENT -> {
                    activity.showCommunicationErrorDialog()
                }
            }
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (this@CommunicatingFragment.endpointId != endpointId) {
                activity.run {
                    disconnect(endpointId)
                    disconnect(this@CommunicatingFragment.endpointId)
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
                            val repayment = try {
                                viewModel.insertRepayment(repaymentPayload)
                            } catch (e: Throwable) {
                                activity.disconnect(endpointId)
                                activity.showErrorOccurredDialog()
                                return@launch
                            }

                            repaymentId = repayment.id

                            activity.sendInserted(endpointId).addOnSuccessListener {
                                state = SenderState.INSERTED
                            }.addOnFailureListener {
                                lifecycleScope.launch {
                                    activity.disconnect(endpointId)
                                    rollback(repayment.id)
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
                            val action = CommunicatingFragmentDirections.actionViewCommunicatingFragmentToSentRepaymentFragment(args.user, args.balances)
                            findNavController().navigate(action)
                        } else {
                            rollback(repaymentId)
                        }
                    }
                }
            }
        }

        override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_communicating, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startDiscoveryTask()
    }

    private fun startDiscoveryTask() {
        fun inner() {
            val activity = activity

            val task = activity.discovery(discoveryCallback)

            task.addOnSuccessListener {
                Log.d("ConnectUserFragment", "start discovering")
            }

            task.addOnFailureListener { e ->
                Log.e("ConnectUserFragment", e.stackTraceToString())
                showFailedDialog()
            }
        }

        confirmPermissions(activity) { hasPermissions, permissions ->
            if (!hasPermissions) {
                if (permissions == null) {
                    showFailedDialog()
                    return@confirmPermissions
                }

                activity.callback = ActivityResultCallback {
                    inner()
                }

                activity.requestPermissions(permissions)
            } else {
                inner()
            }
        }
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

    private suspend fun rollback(id: Long) {
        viewModel.removeRepayment(id)

        activity.showErrorOccurredDialog()
    }

    private fun showFailedDialog() {
        AlertDialog.Builder(activity)
            .setMessage(R.string.fail_to_start_nearby)
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                activity.finish()
                dialog.dismiss()
            }
            .show()
    }
}
