package cloud.runningpig.bearnote.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import cloud.runningpig.bearnote.BearNoteApplication
import cloud.runningpig.bearnote.BearNoteRepository
import cloud.runningpig.bearnote.databinding.FragmentNoteListBinding
import cloud.runningpig.bearnote.logic.dao.BearNoteDatabase

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class NoteListFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var adapter: NLFListAdapter

    val viewModel: DetailViewModel by activityViewModels {
        val repository = BearNoteRepository.getInstance(
            BearNoteDatabase.getDatabase(BearNoteApplication.context).noteCategoryDao(),
            BearNoteDatabase.getDatabase(BearNoteApplication.context).noteDao()
        )
        DetailViewModelFactory(repository)
    }

    private var _binding: FragmentNoteListBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteListBinding.inflate(inflater, container, false)
        adapter = NLFListAdapter { _, noteDetail ->
            val intent = Intent(activity, EditNoteActivity::class.java)
            intent.putExtra("noteDetail", noteDetail)
            startActivity(intent)
        }
        binding.fnlRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.fnlRecyclerView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.queryByDate.observe(this.viewLifecycleOwner) {
            adapter.submitList(it)
        }

//        viewModel.date.observe(this.viewLifecycleOwner) { date: Date ->
//            val startOfDay = viewModel.getStartOfDay(date)
//            val endOfDay = viewModel.getEndOfDay(date)
//            Log.d("test20210906", "date: $date")
//            viewModel.queryByDate(startOfDay, endOfDay).observe(this.viewLifecycleOwner) {
//                Log.d("test20210906", "startOfDay: $startOfDay, endOfDay: $endOfDay")
//
//            }
//        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NoteListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}