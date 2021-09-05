package cloud.runningpig.bearnote.ui.note

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cloud.runningpig.bearnote.BearNoteApplication
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.logic.utils.LogUtil

class TransferFragment : Fragment() {

    companion object {
        fun newInstance() = TransferFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("asdfasdf", "onCreate: $javaClass")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("asdfasdf", "onCreateView: $javaClass")
        LogUtil.d(BearNoteApplication.TAG, "TransferFragment onCreateView: ")
        return inflater.inflate(R.layout.transfer_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("asdfasdf", "onViewCreated: $javaClass")
    }

}