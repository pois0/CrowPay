package jp.pois.crowpay.fragments.balance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import jp.pois.crowpay.databinding.FragmentSentBalanceBinding

class SentBalanceFragment : Fragment() {
    private val args by navArgs<SentBalanceFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding =  FragmentSentBalanceBinding.inflate(inflater, container, false)

        binding.apply {
            balance = args.balance
            user = args.user
            handler = View.OnClickListener {
                activity?.finish()
            }
        }

        return binding.root
    }
}
