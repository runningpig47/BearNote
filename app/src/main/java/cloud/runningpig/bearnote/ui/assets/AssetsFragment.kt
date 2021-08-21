package cloud.runningpig.bearnote.ui.assets

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cloud.runningpig.bearnote.R

class AssetsFragment : Fragment() {

    companion object {
        fun newInstance() = AssetsFragment()
    }

    private lateinit var viewModel: AssetsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.assets_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AssetsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}