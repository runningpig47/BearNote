package cloud.runningpig.bearnote.ui.note

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cloud.runningpig.bearnote.BearNoteApplication
import cloud.runningpig.bearnote.databinding.SpendingFragmentBinding
import cloud.runningpig.bearnote.logic.model.NoteCategory
import cloud.runningpig.bearnote.logic.utils.Injector
import cloud.runningpig.bearnote.logic.utils.ViewUtil
import cloud.runningpig.bearnote.ui.note.category.CategoryActivity
import java.util.*

private const val ARG_PARAM1 = "sort"

/**
 * 支出&收入共用
 */
class SpendingFragment : Fragment() {
    private var param1: Int? = null

    private val viewModel: SpendingViewModel by activityViewModels {
        Injector.providerSpendingViewModelFactory(requireContext())
    }
    private var _binding: SpendingFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getInt(ARG_PARAM1, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SpendingFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ItemList1Adapter { position ->
            if (position != -1) {
                // TODO 考虑拿出来对象
                recyclerViewUp()
                binding.spendingRecyclerView.scrollToPosition(position)
            } else {
                val intent = Intent(context, CategoryActivity::class.java)
                intent.putExtra("page", param1)
                context?.startActivity(intent)
            }
        }
        binding.spendingRecyclerView.adapter = adapter
        viewModel.loadBySort(param1 ?: 0).observe(this.viewLifecycleOwner) {
            val list = LinkedList(it)
            val setting = NoteCategory(// 添加末尾固定的设置按钮
                name = "设置",
                icon = "setting",
                sort = 0,
                order = Int.MAX_VALUE,
                uid = 0,
                recorded = false,
                isUpload = 1
            )
            list.add(setting)
            adapter.submitList(list)
            // 设置-删除所有类别后，回来键盘应该是关闭的
            // TODO 是否需要修改观察的page字段？
            if (it.isEmpty()) {
                recyclerViewDown()
            }
        }
        viewModel.page.observe(this.viewLifecycleOwner) {
            recyclerViewDown()
            val p = adapter.getSelectedPosition()
            adapter.setSelectedPosition(-1)
            adapter.notifyItemChanged(p) // 清除选中背景
        }
    }

    private fun getScreenHeight() = resources.displayMetrics.heightPixels

    /**
     * 让Recycler提高，增加顶部和底部padding
     */
    private fun recyclerViewUp() {
        binding.view11.visibility = View.VISIBLE
        val paddingTop = ViewUtil.dp2px(BearNoteApplication.context, 12F)
        val paddingHorizontal = ViewUtil.dp2px(BearNoteApplication.context, 5F)
        val paddingBottom = (getScreenHeight() / 3.333F).toInt()
        binding.spendingRecyclerView.setPadding(
            paddingHorizontal, paddingTop, paddingHorizontal, paddingBottom
        )
    }

    private fun recyclerViewDown() {
        binding.view11.visibility = View.GONE
        val paddingTop = ViewUtil.dp2px(BearNoteApplication.context, 12F)
        val paddingHorizontal = ViewUtil.dp2px(BearNoteApplication.context, 5F)
        binding.spendingRecyclerView.setPadding(paddingHorizontal, paddingTop, paddingHorizontal, 0)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int) =
            SpendingFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                }
            }
    }

}