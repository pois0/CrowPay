package jp.pois.crowpay.repos.entities

import androidx.room.Embedded

data class RepaymentWithUser(
    @Embedded val repayment: Repayment,
    @Embedded(prefix = "user_") val user: User
)
