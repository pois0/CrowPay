package jp.pois.crowpay.fragments.receive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import jp.pois.crowpay.ReceiveActivity
import jp.pois.crowpay.databinding.FragmentConfirmBalanceBinding

@AndroidEntryPoint
class ConfirmBalanceFragment : Fragment() {
    private val args by navArgs<ConfirmBalanceFragmentArgs>()

    private val activity
        get() = getActivity() as ReceiveActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentConfirmBalanceBinding.inflate(inflater, container, false)

        binding.apply {
            balance = args.balance
            user = args.user
            handler = ClickHandler()
        }

        return binding.root
    }

    inner class ClickHandler {
        fun reject() {
            activity.rejectApplication()
        }

        fun accept() {
            activity.sendApplicationAccepted()
            val action = ConfirmBalanceFragmentDirections.actionViewConfirmBalanceFragmentToCommunicatingFragment()
            findNavController().navigate(action)
        }
    }
}
