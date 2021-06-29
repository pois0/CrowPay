package jp.pois.crowpay.repos.entities

import androidx.room.Embedded

data class RepaymentDetail(
    @Embedded(prefix = "user_") val user: User,
    @Embedded val repayment: Repayment
)
