package jp.pois.crowpay.utils

import androidx.room.TypeConverter

enum class Direction {
    BORROWING, LENDING;

    class Converters {
        @TypeConverter
        fun fromDirection(direction: Direction?): Int? = direction?.let { if (it == BORROWING) 0 else 1 }

        @TypeConverter
        fun toDirection(value: Int?): Direction? = value?.let { if (it == 0) BORROWING else LENDING }
    }
}
