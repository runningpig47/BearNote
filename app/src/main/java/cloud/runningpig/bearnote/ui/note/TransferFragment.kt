package cloud.runningpig.bearnote.ui.note

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cloud.runningpig.bearnote.BearNoteApplication
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.logic.utils.LogUtil

class TransferFragment : Fragment() {

    companion object {
        fun newInstance() = TransferFragment()
    }

    private lateinit var viewModel: TransferViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LogUtil.d(BearNoteApplication.TAG, "TransferFragment onCreateView: ")
        return inflater.inflate(R.layout.transfer_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TransferViewModel::class.java)
        // TODO: Use the ViewModel
    }

}