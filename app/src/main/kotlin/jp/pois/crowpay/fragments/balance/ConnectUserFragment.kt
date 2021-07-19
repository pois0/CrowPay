package jp.pois.crowpay.fragments.balance

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import dagger.hilt.android.AndroidEntryPoint
import jp.pois.crowpay.NewBalanceActivity
import jp.pois.crowpay.R
import jp.pois.crowpay.data.EndpointIdentifier
import jp.pois.crowpay.databinding.FragmentBalanceConnectUserBinding
import jp.pois.crowpay.repos.entities.displayName
import jp.pois.crowpay.ui.UserAdapter
import jp.pois.crowpay.ui.confirmPermissions
import jp.pois.crowpay.utils.decodeProtoBuf
import jp.pois.crowpay.utils.endpointIdentifier
import jp.pois.crowpay.viewmodels.ConnectUserViewModel
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf

@AndroidEntryPoint
class ConnectUserFragment : Fragment() {
    private val viewModel by viewModels<ConnectUserViewModel>()
    private val args by navArgs<ConnectUserFragmentArgs>()

    private val activity: NewBalanceActivity
        get() = getActivity() as NewBalanceActivity

    @OptIn(ExperimentalSerializationApi::class)
    private val discoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            val (uuid, string) = info.endpointIdentifier
            Log.d("ConnectUserFragment:onEndpointFound", "$endpointId, $uuid, $string")
            viewModel.addUser(endpointId, uuid, string)
        }

        override fun onEndpointLost(endpointId: String) {
            viewModel.removeUser(endpointId)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentBalanceConnectUserBinding.inflate(inflater, container, false)

        val userAdapter = UserAdapter(viewModel.userList) { (endpointId, user) ->
            AlertDialog.Builder(activity)
                .setMessage(getString(R.string.confirm_other_party, user.displayName))
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    val action = ConnectUserFragmentDirections.actionViewConnectUserFragmentToCommunicatingFragment(args.balance, user, endpointId)
                    findNavController().navigate(action)
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
                .show()
        }

        binding.userList.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
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
