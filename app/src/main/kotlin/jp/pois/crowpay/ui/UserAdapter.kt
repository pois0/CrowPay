package jp.pois.crowpay.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import jp.pois.crowpay.databinding.ListItemUserBinding
import jp.pois.crowpay.generated.callback.OnClickListener
import jp.pois.crowpay.repos.entities.User
import jp.pois.crowpay.repos.entities.displayName
import jp.pois.crowpay.viewmodels.ConnectUserViewModel
import java.util.*

class UserAdapter(private val userList: ObservableArrayList<ConnectUserViewModel.Data>, private val listener: (ConnectUserViewModel.Data) -> Unit): RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    init {
        userList.addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableArrayList<ConnectUserViewModel.Data>>() {
            override fun onChanged(sender: ObservableArrayList<ConnectUserViewModel.Data>?) = notifyDataSetChanged()

            override fun onItemRangeChanged(
                sender: ObservableArrayList<ConnectUserViewModel.Data>?,
                positionStart: Int,
                itemCount: Int
            ) = notifyItemRangeChanged(positionStart, itemCount)

            override fun onItemRangeInserted(
                sender: ObservableArrayList<ConnectUserViewModel.Data>?,
                positionStart: Int,
                itemCount: Int
            ) = notifyItemRangeInserted(positionStart, itemCount)

            override fun onItemRangeMoved(
                sender: ObservableArrayList<ConnectUserViewModel.Data>?,
                fromPosition: Int,
                toPosition: Int,
                itemCount: Int
            ) = notifyDataSetChanged()

            override fun onItemRangeRemoved(
                sender: ObservableArrayList<ConnectUserViewModel.Data>?,
                positionStart: Int,
                itemCount: Int
            ) = notifyItemRangeRemoved(positionStart, itemCount)
        })
    }

    override fun getItemCount(): Int = userList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
    = ViewHolder(ListItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = userList[position]
        holder.bind(data.user.displayName) { listener(data) }
    }

    class ViewHolder(private val binding: ListItemUserBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(name: String, listener: View.OnClickListener) {
            binding.name = name
            binding.layout.setOnClickListener(listener)
        }
    }
}
