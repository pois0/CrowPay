package jp.pois.crowpay.data

import jp.pois.crowpay.utils.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class EndpointIdentifier(
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID,

    val name: String
)
