package jp.pois.crowpay.repos.entities

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import jp.pois.crowpay.utils.Direction
import jp.pois.crowpay.utils.LocalDateSerializer
import jp.pois.crowpay.utils.UUIDSerializer
import kotlinx.serialization.SerialInfo
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.Transient
import java.time.LocalDate
import java.util.*

@Entity(
    tableName = "repayments",
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["other_party_id"], onDelete = CASCADE)
    ],
    indices = [
        Index("other_party_id"),
        Index("uuid", unique = true)
    ]
)
@Serializable
data class Repayment(
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
    val createdAt: LocalDate = LocalDate.now()
) : java.io.Serializable
