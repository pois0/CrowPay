package jp.pois.crowpay.data

import kotlinx.serialization.Serializable

@Serializable
data class AcceptanceResult(
    val isAccepted: Boolean
)

@Serializable
data class InsertResult(
    val isSucceeded: Boolean
)

@Serializable
data class InconsistencyError(
    val failed: Boolean = true
)
