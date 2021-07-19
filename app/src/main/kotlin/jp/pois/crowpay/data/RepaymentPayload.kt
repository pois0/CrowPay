@file:UseSerializers(UUIDSerializer::class)

package jp.pois.crowpay.data

import jp.pois.crowpay.repos.entities.Repayment
import jp.pois.crowpay.utils.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.util.*

@Suppress("ArrayInDataClass")
@Serializable
data class RepaymentPayload(
    val repayment: Repayment,
    val balances: Array<UUID>
)
