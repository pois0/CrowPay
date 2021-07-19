package jp.pois.crowpay.fragments.receive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import jp.pois.crowpay.databinding.FragmentCompleteBalanceBinding

class CompleteBalanceFragment : Fragment() {
    private val args by navArgs<CompleteBalanceFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentCompleteBalanceBinding.inflate(inflater, container, false)

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
