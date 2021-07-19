package jp.pois.crowpay.fragments.receive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import jp.pois.crowpay.ReceiveActivity
import jp.pois.crowpay.databinding.FragmentConfirmRepaymentBinding
import jp.pois.crowpay.databinding.ListItemBalanceSimpleBinding
import jp.pois.crowpay.repos.entities.Balance
import jp.pois.crowpay.utils.DiffCallbackBase
import jp.pois.crowpay.viewmodels.ConfirmRepaymentViewModel

@AndroidEntryPoint
class ConfirmRepaymentFragment : Fragment() {
    private val args by navArgs<ConfirmRepaymentFragmentArgs>()

    private val viewModel by viewModels<ConfirmRepaymentViewModel>()

    private val activity
        get() = getActivity() as ReceiveActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentConfirmRepaymentBinding.inflate(inflater, container, false)

        val adapter = BalanceAdapter().apply {
            submitList(args.repayment.balances)
        }

        binding.run {
            user = args.user
            sum = - args.repayment.balances.sumOf {
                it.amount
            }
            balanceList.also { rv ->
                rv.adapter = adapter
                rv.layoutManager = LinearLayoutManager(activity)
            }
            handler = ClickHandler()
        }

        return binding.root
    }

    inner class ClickHandler {
        fun reject() {
            activity.finish()
        }

        fun accept() {
            activity.sendApplicationAccepted()
            val action = ConfirmRepaymentFragmentDirections.actionViewConfirmRepaymentFragmentToCommunicatingFragment()
            findNavController().navigate(action)
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
