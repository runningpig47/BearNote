package cloud.runningpig.bearnote.ui.assets

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import cloud.runningpig.bearnote.BearNoteApplication
import cloud.runningpig.bearnote.BearNoteRepository
import cloud.runningpig.bearnote.databinding.AssetsFragmentBinding
import cloud.runningpig.bearnote.logic.dao.BearNoteDatabase
import cloud.runningpig.bearnote.logic.model.Account
import cloud.runningpig.bearnote.ui.detail.DetailViewModel
import cloud.runningpig.bearnote.ui.detail.DetailViewModelFactory
import java.util.*

class AssetsFragment : Fragment() {

    private var _binding: AssetsFragmentBinding? = null
    private val binding get() = _binding!!
    private var accountList: List<Account> = LinkedList()

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
        _binding = AssetsFragmentBinding.inflate(inflater, container, false)
        binding.linearLayout2.setOnClickListener {
            val intent = Intent(activity, SelectIconActivity::class.java)
            startActivity(intent)
        }
        binding.afTextView5.setOnClickListener {
            startActivity(Intent(activity, TransferActivity::class.java))
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = AFList1Adapter { item, _ ->
            val intent = Intent(activity, EditAccountActivity::class.java)
            intent.putExtra("account", item)
            startActivity(intent)
        }
        adapter.setListener(object : AFList1Adapter.List1AdapterListener {
            override fun onItemMove(position: Int, targetPosition: Int) {
                Collections.swap(accountList, position, targetPosition)
                adapter.notifyItemMoved(position, targetPosition)
            }
        })
        binding.afRecyclerView2.adapter = adapter
        viewModel.loadAccount().observe(this.viewLifecycleOwner) {
            if (it.size > 1) {
                binding.afTextView5.visibility = View.VISIBLE
            } else {
                binding.afTextView5.visibility = View.GONE
            }
            accountList = LinkedList(it)
            adapter.submitList(accountList)
        }
        val touchHelper = ItemTouchHelper(List2TouchCallback(adapter))
        touchHelper.attachToRecyclerView(binding.afRecyclerView2)
    }

    /**
     * 在onPause()方法中将所有对List的操作写入数据库，包括item顺序的调整和删除
     * 保证在进入其他页面前，修改写入到数据库中。TODO 性能问题
     */
    override fun onPause() {
        super.onPause()
        // Update item order
        for (i in accountList.indices) {
            accountList[i].order = i
        }
        viewModel.updateList2(accountList)
        Log.d("test20210913", "onPause: ")
    }

}