package jp.pois.crowpay.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import jp.pois.crowpay.databinding.FragmentHistoryBinding

@AndroidEntryPoint
class HistoryFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentHistoryBinding.inflate(inflater, container, false)

        binding.pager.adapter = HistoryFragmentAdapter(this)

        return binding.root
    }

    private class HistoryFragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        @OptIn(ExperimentalStdlibApi::class)
        private val fragments = mapOf(
            BALANCES_PAGE_INDEX to { BalanceListFragment() },
            REPAYMENTS_PAGE_INDEX to { RepaymentListFragment() }
        )

        override fun getItemCount(): Int = fragments.size

        override fun createFragment(position: Int): Fragment = fragments[position]?.invoke() ?: throw IndexOutOfBoundsException()

        companion object {
            const val BALANCES_PAGE_INDEX = 0
            const val REPAYMENTS_PAGE_INDEX = 1
        }
    }
}
