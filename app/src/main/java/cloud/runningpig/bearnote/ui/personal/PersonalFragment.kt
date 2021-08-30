package cloud.runningpig.bearnote.ui.personal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cloud.runningpig.bearnote.R

class PersonalFragment : Fragment() {

    companion object {
        fun newInstance() = PersonalFragment()
    }

    private lateinit var viewModel: PersonalViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.personal_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PersonalViewModel::class.java)
        // TODO: Use the ViewModel
    }

}