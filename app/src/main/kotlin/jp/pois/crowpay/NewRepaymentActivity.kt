package jp.pois.crowpay

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import jp.pois.crowpay.data.InsertResult
import jp.pois.crowpay.data.RepaymentPayload
import jp.pois.crowpay.databinding.ActivityNewRepaymentBinding
import jp.pois.crowpay.utils.endpointIdentifierBytes
import jp.pois.crowpay.utils.p2pDiscoveryOption
import jp.pois.crowpay.utils.send
import jp.pois.crowpay.utils.serviceId

@AndroidEntryPoint
class NewRepaymentActivity : AppCompatActivity() {
    var callback: ActivityResultCallback<Map<String, Boolean>> = ActivityResultCallback<Map<String, Boolean>> {}

    private lateinit var binding: ActivityNewRepaymentBinding
    private val connectionsClient by lazy { Nearby.getConnectionsClient(this) }
    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions(), callback)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setContentView(this, R.layout.activity_new_repayment)
    }

    fun discovery(callback: EndpointDiscoveryCallback): Task<Void> {
        return connectionsClient.startDiscovery(serviceId, callback, p2pDiscoveryOption)
    }

    private inline fun <reified T> send(endpointId: String, content: T) = connectionsClient.send(endpointId, content)

    fun acceptUser(endpointId: String, callback: ConnectionLifecycleCallback) {
        connectionsClient.requestConnection(endpointIdentifierBytes, endpointId, callback)
    }

    fun acceptConnection(endpointId: String, callback: PayloadCallback) {
        connectionsClient.acceptConnection(endpointId, callback)
    }

    fun requestPermissions(permissions: Array<String>) {
        permissionLauncher.launch(permissions)
    }

    fun sendRepayment(endpointId: String, repaymentPayload: RepaymentPayload) = send(endpointId, repaymentPayload)

    fun sendInserted(endpointId: String) = send(endpointId, InsertResult(true))

    fun disconnect(endpointId: String) {
        connectionsClient.disconnectFromEndpoint(endpointId)
    }
}
