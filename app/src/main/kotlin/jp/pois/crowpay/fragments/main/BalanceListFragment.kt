package jp.pois.crowpay.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import jp.pois.crowpay.databinding.FragmentBalanceListBinding
import jp.pois.crowpay.databinding.ListItemBalanceBinding
import jp.pois.crowpay.repos.entities.BalanceWithUser
import jp.pois.crowpay.utils.DiffCallbackBase
import jp.pois.crowpay.viewmodels.BalanceListViewModel

@AndroidEntryPoint
class BalanceListFragment : Fragment() {
    private val viewModel by viewModels<BalanceListViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentBalanceListBinding.inflate(inflater, container, false)
        if (context == null) return binding.root

        val adapter = BalanceAdapter()
        binding.recyclerview.also { v ->
            v.adapter = adapter
            v.layoutManager = LinearLayoutManager(activity)
        }

        viewModel.balances.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        return binding.root
    }

    private class BalanceAdapter : ListAdapter<BalanceWithUser, BalanceAdapter.ViewHolder>(DiffCallback) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                = ViewHolder(ListItemBalanceBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val balance = getItem(position)
            holder.bind(balance)
        }

        class ViewHolder(private val binding: ListItemBalanceBinding) : RecyclerView.ViewHolder(binding.root) {
            init {
                binding.setClickListener {

                }
            }

            fun bind(item: BalanceWithUser) {
                binding.apply {
                    balance = item
                }.executePendingBindings()
            }
        }

        private object DiffCallback : DiffCallbackBase<BalanceWithUser>() {
            override fun BalanceWithUser.id(): Long = balance.id
        }
    }
}
