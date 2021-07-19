package jp.pois.crowpay.utils

import androidx.recyclerview.widget.DiffUtil
import java.util.*

abstract class DiffCallbackBase<T> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem.id() == newItem.id()

    override fun areContentsTheSame(oldItem: T, newItem: T) = Objects.equals(oldItem, newItem)

    protected abstract fun T.id(): Long
}
