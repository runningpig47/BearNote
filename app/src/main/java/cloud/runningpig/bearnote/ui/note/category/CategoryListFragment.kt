package cloud.runningpig.bearnote.ui.note.category

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
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.databinding.FragmentCategoryListBinding
import cloud.runningpig.bearnote.logic.model.NoteCategory
import cloud.runningpig.bearnote.logic.utils.Injector
import cloud.runningpig.bearnote.logic.utils.LogUtil
import cloud.runningpig.bearnote.ui.note.SpendingViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "sort"

class CategoryListFragment : Fragment() {

    private var _binding: FragmentCategoryListBinding? = null
    private val binding get() = _binding!!
    private var param1: Int? = null
    private var noteCategoryList: LinkedList<NoteCategory> = LinkedList()
    private var deleteList: ArrayList<NoteCategory> = ArrayList()
    private lateinit var adapter: CategoryList1Adapter

    private val viewModel: SpendingViewModel by activityViewModels {
        Injector.providerSpendingViewModelFactory(requireContext())
    }

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
        adapter = CategoryList1Adapter {
            val intent = Intent(activity, AddCategoryActivity::class.java)
            intent.putExtra("noteCategoryId", it)
            startActivity(intent)
        }
        adapter.setListener(object : CategoryList1Adapter.List1AdapterListener {
            override fun onItemMove(position: Int, targetPosition: Int) {
                Collections.swap(noteCategoryList, position, targetPosition)
                adapter.notifyItemMoved(position, targetPosition)
            }

            override fun onItemDelete(position: Int) {
                try {
                    // 删除之前检查是否存在记账，如果删除类别会将类别下的记账一并删除
                    val deleteItem = noteCategoryList[position]
                    Log.d("test211002", "deleteItem: $deleteItem")
                    MainScope().launch(Dispatchers.IO) {
                        val count = viewModel.countCid(deleteItem.id)
                        withContext(Dispatchers.Main) {
                            if (count != null) { // 如果count固定返回1，说明该类别下存在记账
                                // 弹窗确认是否删除该类别下的所有记账信息
                                showConfirmationDialog(position)
                            } else { // 如果count<=0，说明类别下不存在记账，可以直接删除类别
                                // 删除的item需要单独保存，因为它们无法通过updateDao更新
                                deleteCategoryItem(position)
                            }
                        }
                    }
                } catch (e: Exception) {
                    // TODO 快速点击删除按钮时，出现数组越界-1
                    LogUtil.e(BearNoteApplication.TAG, e.stackTraceToString())
                }
            }
        })
        binding.fclRecyclerView.adapter = adapter
        viewModel.loadBySort(param1 ?: 0).observe(this.viewLifecycleOwner) {
            noteCategoryList = LinkedList(it)
            adapter.submitList(noteCategoryList)
        }
        val touchHelper = ItemTouchHelper(List1TouchCallback(adapter))
        touchHelper.attachToRecyclerView(binding.fclRecyclerView)
    }

    private fun showConfirmationDialog(position: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                val deleteItem = noteCategoryList[position]
                // 0级协程：和另一个0级同时进行，协程和主线程同时运行
                MainScope().launch(Dispatchers.IO) {
                    viewModel.deleteNoteCategory(deleteItem.id)
                    withContext(Dispatchers.Main) {
                        deleteCategoryItem(position)
                    }
                }
            }
            .show()
    }

    private fun deleteCategoryItem(position: Int) {
        val deleteItem = noteCategoryList.removeAt(position)
        deleteList.add(deleteItem)
        adapter.notifyItemRemoved(position)
    }

    /**
     * 在onPause()方法中将所有对List的操作写入数据库，包括item顺序的调整和删除
     * 保证在进入其他页面前，修改写入到数据库中。TODO 性能问题，每次onPause都写回？即便没改
     */
    override fun onPause() {
        super.onPause()
        for (i in 0 until noteCategoryList.size) { // Update item order
            noteCategoryList[i].order = i
        }
        viewModel.updateList(noteCategoryList)
        viewModel.delete(deleteList) // Delete item
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