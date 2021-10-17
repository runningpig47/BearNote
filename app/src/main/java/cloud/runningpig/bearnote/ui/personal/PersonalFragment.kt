package cloud.runningpig.bearnote.ui.personal

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cloud.runningpig.bearnote.BearNoteApplication
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.databinding.PersonalFragmentBinding
import cloud.runningpig.bearnote.logic.BearNoteRepository
import cloud.runningpig.bearnote.logic.dao.BearNoteDatabase
import cloud.runningpig.bearnote.ui.detail.DetailViewModel
import cloud.runningpig.bearnote.ui.detail.DetailViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PersonalFragment : Fragment() {

    private var _binding: PersonalFragmentBinding? = null
    private val binding get() = _binding!!

    val viewModel: DetailViewModel by activityViewModels {
        val repository = BearNoteRepository.getInstance(
            BearNoteDatabase.getDatabase(BearNoteApplication.context).noteCategoryDao(),
            BearNoteDatabase.getDatabase(BearNoteApplication.context).noteDao()
        )
        DetailViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PersonalFragmentBinding.inflate(inflater, container, false)
        viewModel.selectUser().observe(this.viewLifecycleOwner) {
            if (it != null) {
                binding.textView1.text = it.nickname
                binding.imageView1.visibility = View.VISIBLE
                binding.textView1.isEnabled = false
                binding.imageView1.setOnClickListener {
                    showConfirmationDialog()
                }
            } else {
                binding.textView1.text = "登录"
                binding.imageView1.visibility = View.INVISIBLE
                binding.textView1.isEnabled = true
                binding.textView1.setOnClickListener {
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        return binding.root
    }

    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question4))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.deleteUser()
            }
            .show()
    }

}