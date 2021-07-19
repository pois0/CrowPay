package jp.pois.crowpay.fragments.repayment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import jp.pois.crowpay.data.totalAmount
import jp.pois.crowpay.databinding.FragmentSentRepaymentBinding
import jp.pois.crowpay.databinding.ListItemBalanceSimpleBinding
import jp.pois.crowpay.repos.entities.Balance
import jp.pois.crowpay.utils.DiffCallbackBase

@AndroidEntryPoint
class SentRepaymentFragment : Fragment() {
    private val args by navArgs<SentRepaymentFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentSentRepaymentBinding.inflate(inflater, container, false)

        val adapter = BalanceAdapter().apply {
            submitList(args.balances.balances)
        }

        binding.run {
            sum = args.balances.totalAmount
            user = args.user
            balanceList.also { rv ->
                rv.layoutManager = LinearLayoutManager(activity)
                rv.adapter = adapter
            }
            okButton.setOnClickListener {
                activity?.finish()
            }
        }

        return binding.root
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
