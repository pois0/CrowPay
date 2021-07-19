package jp.pois.crowpay.utils

import android.content.res.Resources
import android.util.TypedValue
import android.util.TypedValue.applyDimension

val Float.dp get() = applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)
val Float.sp get() = applyDimension(TypedValue.COMPLEX_UNIT_SP, this, Resources.getSystem().displayMetrics)
val Int.dp get() = toFloat().dp.toInt()
val Int.sp get() = toFloat().sp.toInt()
