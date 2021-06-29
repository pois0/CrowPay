package jp.pois.crowpay.repos.entities

import androidx.room.Embedded
import androidx.room.Relation

data class BalanceDetail(
    @Embedded val balance: Balance,
    @Embedded(prefix = "user_") val user: User,
    @Embedded(prefix = "repayment_") val repayment: Repayment?
)
