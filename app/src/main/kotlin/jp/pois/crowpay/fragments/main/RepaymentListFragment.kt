package jp.pois.crowpay.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import jp.pois.crowpay.databinding.FragmentBalanceListBinding
import jp.pois.crowpay.databinding.ListItemRepaymentBinding
import jp.pois.crowpay.repos.entities.RepaymentWithUser
import jp.pois.crowpay.utils.DiffCallbackBase
import jp.pois.crowpay.viewmodels.RepaymentListViewModel

@AndroidEntryPoint
class RepaymentListFragment : Fragment() {
    private val viewModel by viewModels<RepaymentListViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentBalanceListBinding.inflate(inflater, container, false)
        if (context == null) return binding.root

        val adapter = RepaymentAdapter()
        binding.recyclerview.adapter = adapter

        viewModel.repayments.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        return binding.root
    }

    private class RepaymentAdapter : ListAdapter<RepaymentWithUser, RepaymentAdapter.ViewHolder>(DiffCallback) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                = ViewHolder(ListItemRepaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val balance = getItem(position)
            holder.bind(balance)
        }

        class ViewHolder(private val binding: ListItemRepaymentBinding) : RecyclerView.ViewHolder(binding.root) {
            init {
                binding.setClickListener {

                }
            }

            fun bind(item: RepaymentWithUser) {
                binding.apply {
                    repayment = item
                }.executePendingBindings()
            }
        }

        private object DiffCallback : DiffCallbackBase<RepaymentWithUser>() {
            override fun RepaymentWithUser.id(): Long = repayment.id
        }
    }
}
