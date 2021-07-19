package jp.pois.crowpay.fragments.repayment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import jp.pois.crowpay.databinding.FragmentSelectUserBinding
import jp.pois.crowpay.databinding.ListItemUserBinding
import jp.pois.crowpay.repos.entities.User
import jp.pois.crowpay.repos.entities.displayName
import jp.pois.crowpay.utils.DiffCallbackBase
import jp.pois.crowpay.viewmodels.SetRepaymentViewModel

@AndroidEntryPoint
class SelectUserFragment : Fragment() {
    val viewModel by viewModels<SetRepaymentViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentSelectUserBinding.inflate(inflater, container, false)

        val adapter = UserAdapter()

        binding.userList.also { v ->
            v.adapter = adapter
            v.layoutManager = LinearLayoutManager(activity)
        }

        viewModel.users.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        return binding.root
    }

    private inner class UserAdapter : ListAdapter<User, ViewHolder>(DiffCallback) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                = ViewHolder(ListItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val balance = getItem(position)
            holder.bind(balance)
        }
    }

    private object DiffCallback : DiffCallbackBase<User>() {
        override fun User.id(): Long = id
    }

    inner class ViewHolder(private val binding: ListItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: User) {
            binding.apply {
                name = item.displayName
                layout.setOnClickListener {
                    val action = SelectUserFragmentDirections.actionViewSelectUserFragmentToSelectBalancesFragment(item)
                    findNavController().navigate(action)
                }
            }.executePendingBindings()
        }
    }
}
