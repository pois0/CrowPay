package jp.pois.crowpay.repos.entities

import androidx.room.Embedded

data class BalanceWithUser(
    @Embedded val balance: Balance,
    @Embedded(prefix = "user_") val user: User
)
