package jp.pois.crowpay.fragments.receive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import jp.pois.crowpay.databinding.FragmentCommunicatingBinding

class CommunicatingFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentCommunicatingBinding.inflate(inflater, container, false).root
    }
}
