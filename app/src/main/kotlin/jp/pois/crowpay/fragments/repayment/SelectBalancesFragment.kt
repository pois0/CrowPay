package jp.pois.crowpay.fragments.repayment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import jp.pois.crowpay.R
import jp.pois.crowpay.data.Balances
import jp.pois.crowpay.databinding.FragmentSelectBalancesBinding
import jp.pois.crowpay.databinding.ListItemCheckingBalanceBinding
import jp.pois.crowpay.repos.entities.Balance
import jp.pois.crowpay.utils.DiffCallbackBase
import jp.pois.crowpay.viewmodels.SelectBalancesViewModel

@AndroidEntryPoint
class SelectBalancesFragment : Fragment() {
    private val args by navArgs<SelectBalancesFragmentArgs>()
    private val viewModel by viewModels<SelectBalancesViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentSelectBalancesBinding.inflate(inflater, container, false)

        val adapter = BalanceAdapter()

        binding.balanceList.also { v ->
            v.adapter = adapter
            v.layoutManager = LinearLayoutManager(activity)
        }

        binding.ok.setOnClickListener {
            if (viewModel.checkedBalances.isEmpty()) {
                AlertDialog.Builder(activity)
                    .setMessage(R.string.balance_at_least_one)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok) { _, _, -> }
                    .show()
                return@setOnClickListener
            }
            val action = SelectBalancesFragmentDirections.actionViewSelectBalancesFragmentToSenderConfirmRepaymentFragment(args.user, Balances(viewModel.checkedBalances))
            findNavController().navigate(action)
        }

        viewModel.getBalances(args.user.id).observe(viewLifecycleOwner) {
            Log.d("selectbala", it.joinToString())
            adapter.submitList(it)
        }

        return binding.root
    }

    private inner class BalanceAdapter : ListAdapter<Balance, ViewHolder>(DiffCallback) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                = ViewHolder(ListItemCheckingBalanceBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val balance = getItem(position)
            holder.bind(balance)
        }
    }

    private object DiffCallback : DiffCallbackBase<Balance>() {
        override fun Balance.id(): Long = id
    }

    inner class ViewHolder(private val binding: ListItemCheckingBalanceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Balance) {
            binding.apply {
                balance = item
                checkbox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        viewModel.checkedBalances.add(item)
                    } else {
                        viewModel.checkedBalances.remove(item)
                    }
                }

                clickListener = View.OnClickListener {
                    checkbox.toggle()
                }
            }.executePendingBindings()
        }
    }
}
