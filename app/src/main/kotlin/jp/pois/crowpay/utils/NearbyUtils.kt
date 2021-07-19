package jp.pois.crowpay.utils

import com.google.android.gms.nearby.connection.*
import com.google.android.gms.tasks.Task
import jp.pois.crowpay.data.EndpointIdentifier

const val serviceId = "jp.pois.crowpay.balance"

val p2pDiscoveryOption = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()

val p2pAdvertiseOption = AdvertisingOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()

inline fun <reified T> ConnectionsClient.send(endpointId: String, content: T) =
    sendPayload(endpointId, Payload.fromBytes(encodeToProtoBuf(content)))

val DiscoveredEndpointInfo.endpointIdentifier: EndpointIdentifier
    get() = decodeProtoBuf(endpointInfo)
