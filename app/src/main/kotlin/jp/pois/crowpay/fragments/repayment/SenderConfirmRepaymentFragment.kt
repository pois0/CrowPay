package jp.pois.crowpay.fragments.repayment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import jp.pois.crowpay.data.totalAmount
import jp.pois.crowpay.databinding.FragmentSenderConfirmRepaymentBinding
import jp.pois.crowpay.databinding.ListItemBalanceSimpleBinding
import jp.pois.crowpay.repos.entities.Balance
import jp.pois.crowpay.utils.DiffCallbackBase

@AndroidEntryPoint
class SenderConfirmRepaymentFragment : Fragment() {
    private val args by navArgs<SenderConfirmRepaymentFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentSenderConfirmRepaymentBinding.inflate(inflater, container, false)

        val adapter = BalanceAdapter().apply {
            submitList(args.balances.balances)
        }

        binding.run {
            balanceList.also { rv ->
                rv.adapter = adapter
                rv.layoutManager = LinearLayoutManager(activity)
            }
            user = args.user
            sum = args.balances.totalAmount
            handler = ClickHandler()
        }

        return binding.root
    }

    inner class ClickHandler {
        fun accept() {
            val action = SenderConfirmRepaymentFragmentDirections.actionViewSenderConfirmRepaymentFragmentToCommunicatingFragment(
                args.user,
                args.balances
            )
            findNavController().navigate(action)
        }

        fun reject() {
            activity?.finish()
        }
    }

    private inner class BalanceAdapter : ListAdapter<Balance, ViewHolder>(DiffCallback) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                = ViewHolder(ListItemBalanceSimpleBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val balance = getItem(position)
            holder.bind(balance)
        }
    }

    private object DiffCallback : DiffCallbackBase<Balance>() {
        override fun Balance.id(): Long = id
    }

    inner class ViewHolder(private val binding: ListItemBalanceSimpleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Balance) {
            binding.apply {
                balance = item
            }.executePendingBindings()
        }
    }
}
