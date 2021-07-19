package jp.pois.crowpay.viewmodels

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import jp.pois.crowpay.repos.entities.Balance
import jp.pois.crowpay.utils.toLocalDate
import jp.pois.crowpay.utils.toText
import java.time.LocalDate

class SetBalanceViewModel : ViewModel() {
    val amountString = ObservableField("")
    val createdAtString = ObservableField(LocalDate.now().toText)
    val deadlineString = ObservableField("")
    val remark = ObservableField("")

    val amount: Int
        get() = amountString.get()?.toIntOrNull() ?: 0

    val createdAt: LocalDate
        get() = createdAtString.get()?.runCatching(::toLocalDate)?.getOrNull() ?: LocalDate.now()

    val deadline: LocalDate?
        get() = deadlineString.get()?.takeUnless { it.isEmpty() }?.runCatching(::toLocalDate)?.getOrNull()

    fun toBalance() = Balance(
        createdAt = createdAt,
        deadline = deadline,
        amount = amount,
        remark = remark.get().orEmpty()
    )
}
