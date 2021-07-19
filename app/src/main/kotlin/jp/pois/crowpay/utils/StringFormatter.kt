package jp.pois.crowpay.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.abs

private val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

val Int.amountToText: String
    get() = String.format("%,d", abs(this))

val LocalDate.toText: String
    get() = dateFormatter.format(this)

val LocalDate?.toTextOrEmpty: String
    get() = this?.let(dateFormatter::format).orEmpty()

fun toLocalDate(raw: String) = LocalDate.parse(raw, dateFormatter)
