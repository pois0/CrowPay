package jp.pois.crowpay.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import jp.pois.crowpay.databinding.FragmentUserListBinding

@AndroidEntryPoint
class UserListFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentUserListBinding.inflate(inflater, container, false)
        return binding.root
    }
}
