package jp.pois.crowpay.fragments.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.pois.crowpay.NewBalanceActivity
import jp.pois.crowpay.NewRepaymentActivity
import jp.pois.crowpay.R
import jp.pois.crowpay.ReceiveActivity
import jp.pois.crowpay.databinding.FragmentHomeBinding
import jp.pois.crowpay.utils.dp
import jp.pois.crowpay.viewmodels.HomeViewModel

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel by viewModels<HomeViewModel>()

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentHomeBinding.inflate(inflater, container, false)

        this.binding = binding
        binding.handler = HomeFragmentViewHandler()
        return binding.root
    }

    inner class HomeFragmentViewHandler {
        private var isMenuOpen = false

        fun onClickMenu() {
            val binding = binding
            if (isMenuOpen) {
                binding.floatingMenu.setImageResource(R.drawable.ic_baseline_menu)

                binding.newBalance.apply {
                    animate().translationY(0F).withEndAction { visibility = View.GONE }
                }
                binding.newRepayment.apply {
                    animate().translationY(0F).withEndAction { visibility = View.GONE }
                }
                binding.receive.apply{
                    animate().translationY(0F).withEndAction { visibility = View.GONE }
                }

            } else {
                binding.floatingMenu.setImageResource(R.drawable.ic_baseline_expand_more)

                binding.newBalance.visibility = View.VISIBLE
                binding.newRepayment.visibility = View.VISIBLE
                binding.receive.visibility = View.VISIBLE

                binding.newBalance.animate().translationY((-65F).dp)
                binding.newRepayment.animate().translationY((-115F).dp)
                binding.receive.animate().translationY((-165F).dp)
            }
            isMenuOpen = !isMenuOpen
        }

        fun onClickNewBalance() {
            startActivity(Intent(context, NewBalanceActivity::class.java))
        }

        fun onClickNewRepayment() {
            startActivity(Intent(context, NewRepaymentActivity::class.java))
        }

        fun onClickReceive() {
            startActivity(Intent(context, ReceiveActivity::class.java))
        }
    }
}
