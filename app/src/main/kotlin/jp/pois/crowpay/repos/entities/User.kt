package jp.pois.crowpay.repos.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(
    tableName = "users",
    indices = [
        Index("uuid", unique = true)
    ]
)
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val uuid: UUID,
    @ColumnInfo(name = "customized_name") val customizedName: String? = null
) : Serializable

val User.displayName: String
    get() = customizedName ?: name
