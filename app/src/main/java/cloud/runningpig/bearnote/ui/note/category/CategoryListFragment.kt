package cloud.runningpig.bearnote.ui.note.category

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import cloud.runningpig.bearnote.BearNoteApplication
import cloud.runningpig.bearnote.databinding.FragmentCategoryListBinding
import cloud.runningpig.bearnote.logic.model.NoteCategory
import cloud.runningpig.bearnote.logic.utils.Injector
import cloud.runningpig.bearnote.logic.utils.LogUtil
import cloud.runningpig.bearnote.ui.note.SpendingViewModel
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "sort"

class CategoryListFragment : Fragment() {

    private var param1: Int? = null
    private var noteCategoryList: LinkedList<NoteCategory> = LinkedList()
    private var deleteList: ArrayList<NoteCategory> = ArrayList()

    private val viewModel: SpendingViewModel by activityViewModels {
        Injector.providerSpendingViewModelFactory(requireContext())
    }

    private var _binding: FragmentCategoryListBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getInt(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = CategoryList1Adapter()
        adapter.setListener(object : CategoryList1Adapter.List1AdapterListener {
            override fun onItemMove(position: Int, targetPosition: Int) {
//                noteCategoryList[position].order = targetPosition
//                noteCategoryList[targetPosition].order = position
                Collections.swap(noteCategoryList, position, targetPosition)
                adapter.notifyItemMoved(position, targetPosition)
            }

            override fun onItemDelete(position: Int) {
                try {
                    val deleteItem = noteCategoryList.removeAt(position)
                    // 删除的item需要单独保存，因为它们无法通过updateDao更新
                    deleteList.add(deleteItem)
                    adapter.notifyItemRemoved(position)
                } catch (e: Exception) {
                    // TODO 快速点击删除按钮时，出现数组越界-1
                    LogUtil.e(BearNoteApplication.TAG, e.stackTraceToString())
                }
            }
        })
        binding.fclRecyclerView.adapter = adapter
        binding.fclRecyclerView.layoutManager = LinearLayoutManager(context)
        viewModel.loadBySort(param1 ?: 0).observe(this.viewLifecycleOwner) {
            LogUtil.d("test", "新的通知")
            noteCategoryList = LinkedList(it)
            adapter.submitList(noteCategoryList)
        }
        val touchHelper = ItemTouchHelper(List1TouchCallback(adapter))
        touchHelper.attachToRecyclerView(binding.fclRecyclerView)
    }

    /**
     * 在onPause()方法中将所有对List的操作写入数据库，包括item顺序的调整和删除
     * 保证在进入其他页面前，修改写入到数据库中。TODO 性能问题
     */
    override fun onPause() {
        super.onPause()
        // Update item order
        for (i in 0 until noteCategoryList.size) {
            noteCategoryList[i].order = i
        }
        viewModel.updateList(noteCategoryList)
        // Delete item
        viewModel.delete(deleteList)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int) =
            CategoryListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                }
            }
    }

}