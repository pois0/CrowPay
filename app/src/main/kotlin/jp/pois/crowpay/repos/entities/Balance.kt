package jp.pois.crowpay.repos.entities

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import androidx.room.ForeignKey.SET_NULL
import jp.pois.crowpay.utils.LocalDateSerializer
import jp.pois.crowpay.utils.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.time.LocalDate
import java.util.*

@Entity(
    tableName = "balances",
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["other_party_id"], onDelete = CASCADE),
        ForeignKey(entity = Repayment::class, parentColumns = ["id"], childColumns = ["repaid_by"], onDelete = SET_NULL)
    ],
    indices = [
        Index("other_party_id"),
        Index("repaid_by"),
        Index("uuid", unique = true)
    ]
)
@Serializable
data class Balance(
    @PrimaryKey(autoGenerate = true)
    @Transient
    val id: Long = 0,

    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID = UUID.randomUUID(),

    @ColumnInfo(name = "other_party_id")
    @Transient
    val otherPartyId: Long = 0,

    val amount: Int,

    @ColumnInfo(name = "created_at")
    @Serializable(with = LocalDateSerializer::class)
    val createdAt: LocalDate = LocalDate.now(),

    @Serializable(with = LocalDateSerializer::class)
    val deadline: LocalDate? = null,

    @ColumnInfo(name = "repaid_by")
    @Transient
    val repaidBy: Long? = null,

    @Transient
    val remark: String = "",
) : java.io.Serializable

val Balance.isRepaid: Boolean
    get() = repaidBy != null

val Balance.isDebt: Boolean
    get() = amount < 0
