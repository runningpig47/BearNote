package cloud.runningpig.bearnote.ui.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cloud.runningpig.bearnote.R

class TransferFragment : Fragment() {

    companion object {
        fun newInstance() = TransferFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.transfer_fragment, container, false)
    }

}