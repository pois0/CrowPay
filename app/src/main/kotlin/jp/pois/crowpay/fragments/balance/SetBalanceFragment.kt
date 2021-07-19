package jp.pois.crowpay.fragments.balance

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import jp.pois.crowpay.R
import jp.pois.crowpay.databinding.FragmentSetBalanceBinding
import jp.pois.crowpay.utils.toText
import jp.pois.crowpay.viewmodels.SetBalanceViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class SetBalanceFragment : Fragment() {
    private val viewModel by viewModels<SetBalanceViewModel>()
    private lateinit var binding: FragmentSetBalanceBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSetBalanceBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.handlers = OnClickHandlers()

        return binding.root
    }

    private fun validate(): Boolean {
        if (viewModel.amount <= 0) {
            binding.amountInputContainer.error = getString(R.string.amount_error)
            return false
        }

        val createdAt = viewModel.createdAt
        val deadline = viewModel.deadline

        if (deadline != null && deadline < createdAt) {
            binding.deadlineInputContainer.error = getString(R.string.deadline_error)
            return false
        }

        return true
    }

    inner class OnClickHandlers {
        fun next() {
            if (!validate()) return
            val balance = viewModel.toBalance()
            val action = SetBalanceFragmentDirections.actionViewSetBalanceFragmentToConnectUserFragment(balance)
            findNavController().navigate(action)
        }

        fun createdAt() {
            getDatePicker(viewModel.createdAt, viewModel.createdAtString::set).show(childFragmentManager, "Tag")
        }

        fun deadline() {
            val defaultValue = viewModel.run { deadline ?: createdAt }
            getDatePicker(defaultValue, viewModel.deadlineString::set).show(childFragmentManager, "Tag")
        }

        private inline fun getDatePicker(defaultValue: LocalDate, crossinline field: (String) -> Unit): MaterialDatePicker<Long> {
            return MaterialDatePicker.Builder.datePicker()
                .setSelection(defaultValue.atStartOfDay(utcId).toInstant().toEpochMilli())
                .build()
                .apply {
                    addOnPositiveButtonClickListener {
                        val text = Instant.ofEpochMilli(it).atZone(utcId).toLocalDate().toText
                        field(text)
                        Log.d("datepicker", text)
                    }
                }
        }
    }

    companion object {
        private val utcId = ZoneId.of("UTC")
    }
}
