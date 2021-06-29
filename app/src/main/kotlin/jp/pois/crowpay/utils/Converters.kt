package jp.pois.crowpay.utils

import androidx.room.TypeConverter
import java.nio.ByteBuffer
import java.time.LocalDate
import java.util.*

class LocalDateConverters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): Long? = date?.toEpochDay()

    @TypeConverter
    fun toLocalDate(value: Long?): LocalDate? = value?.let(LocalDate::ofEpochDay)
}

class UUIDConverters {
    @TypeConverter
    fun fromUUID(uuid: UUID?): ByteArray? = uuid?.run {
        val buf = ByteBuffer.allocate(16)
        buf.putLong(mostSignificantBits)
        buf.putLong(leastSignificantBits)
        buf.flip()
        buf.array()
    }

    @Suppress("UsePropertyAccessSyntax")
    @TypeConverter
    fun toUUID(value: ByteArray?): UUID? = value?.run {
        val buf = ByteBuffer.wrap(value)
        UUID(buf.getLong(), buf.getLong())
    }
}
