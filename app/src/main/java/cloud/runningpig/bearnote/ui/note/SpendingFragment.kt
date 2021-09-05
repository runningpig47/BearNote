package cloud.runningpig.bearnote.ui.note

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cloud.runningpig.bearnote.BearNoteApplication
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.databinding.SpendingFragmentBinding
import cloud.runningpig.bearnote.logic.model.NoteCategory
import cloud.runningpig.bearnote.logic.utils.Injector
import cloud.runningpig.bearnote.logic.utils.LogUtil
import cloud.runningpig.bearnote.logic.utils.ViewUtil
import cloud.runningpig.bearnote.ui.custom.InputDialogFragment
import cloud.runningpig.bearnote.ui.note.category.CategoryActivity
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.view.TimePickerView
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_PARAM1 = "sort"

/**
 * 支出&收入共用
 */
class SpendingFragment : Fragment(), View.OnClickListener {
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
        val adapter = ItemList1Adapter { position, item ->
            if (position != -1) {
                viewModel.item = item
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
            Log.d("test20210904", "viewModel.page: $it, adapter.getSelectedPosition: $p")
            adapter.setSelectedPosition(-1)
            adapter.notifyItemChanged(p) // 清除选中背景
        }
        val inputEditText = binding.view11.view12.inputEditText
        inputEditText.isFocusable = false
        inputEditText.setOnClickListener {
            val newFragment = InputDialogFragment()
            newFragment.setOnBindViewListener(object : InputDialogFragment.OnBindViewListener {
                override fun bindView(view: View) {
                    val editText = view.findViewById<EditText>(R.id.input_editText)
                    editText.isFocusable = true
                    editText.isFocusableInTouchMode = true
                    editText.requestFocus()
                    editText.post {
                        val imm =
                            BearNoteApplication.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.showSoftInput(editText, 0)
                    }
                }
            })
            val manager = childFragmentManager
            val transaction = manager.beginTransaction()
            transaction.add(newFragment, "")
            transaction.commitAllowingStateLoss()
        }
        viewModel.amount.observe(this.viewLifecycleOwner) {
            binding.view11.view12.amountTextView.text = it.toString()
        }
        viewModel.note.observe(this.viewLifecycleOwner) {
            binding.view11.view12.inputEditText.setText(it)
        }

        binding.view11.textView11.setOnClickListener(this) // 0
        binding.view11.textView8.setOnClickListener(this) // 1
        binding.view11.textView9.setOnClickListener(this) // 2
        binding.view11.textView10.setOnClickListener(this) // 3
        binding.view11.textView5.setOnClickListener(this) // 4
        binding.view11.textView6.setOnClickListener(this) // 5
        binding.view11.textView7.setOnClickListener(this) // 6
        binding.view11.textView1.setOnClickListener(this) // 7
        binding.view11.textView2.setOnClickListener(this) // 8
        binding.view11.textView4.setOnClickListener(this) // 9
        binding.view11.imageView2.setOnClickListener(this) // back
        binding.view11.textView15.setOnClickListener(this) // +
        binding.view11.textView14.setOnClickListener(this) // -
        binding.view11.textView12.setOnClickListener(this) // .
        binding.view11.textView13.setOnClickListener(this) // 完成
        binding.view11.dateLayout.setOnClickListener(this) // dateLayout

        initCustomTimePicker()
    }

    private fun getScreenHeight() = resources.displayMetrics.heightPixels

    /**
     * 让Recycler提高，增加顶部和底部padding
     */
    private fun recyclerViewUp() {
        binding.view11.root.visibility = View.VISIBLE
        val paddingTop = ViewUtil.dp2px(BearNoteApplication.context, 12F)
        val paddingHorizontal = ViewUtil.dp2px(BearNoteApplication.context, 5F)
        val paddingBottom = (getScreenHeight() / 3.333F).toInt()
        binding.spendingRecyclerView.setPadding(
            paddingHorizontal, paddingTop, paddingHorizontal, paddingBottom
        )
    }

    private fun recyclerViewDown() {
        binding.view11.root.visibility = View.GONE
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

    override fun onClick(view: View?) {
        var amount = viewModel.amount.value
        if (TextUtils.isEmpty(amount)) {
            viewModel.amount.value = "0"
            amount = "0"
        }
        val stringBuilder = StringBuilder()
        if (amount!!.toDouble() == 0.0) {
            stringBuilder.append("")
        } else {
            stringBuilder.append(amount)
        }
        when (view?.id) {
            R.id.textView11 -> {
                viewModel.amount.value = stringBuilder.append("0").toString()
            }
            R.id.textView8 -> {
                viewModel.amount.value = stringBuilder.append("1").toString()
            }
            R.id.textView9 -> {
                viewModel.amount.value = stringBuilder.append("2").toString()
            }
            R.id.textView10 -> {
                viewModel.amount.value = stringBuilder.append("3").toString()
            }
            R.id.textView5 -> {
                viewModel.amount.value = stringBuilder.append("4").toString()
            }
            R.id.textView6 -> {
                viewModel.amount.value = stringBuilder.append("5").toString()
            }
            R.id.textView7 -> {
                viewModel.amount.value = stringBuilder.append("6").toString()
            }
            R.id.textView1 -> {
                viewModel.amount.value = stringBuilder.append("7").toString()
            }
            R.id.textView2 -> {
                viewModel.amount.value = stringBuilder.append("8").toString()
            }
            R.id.textView4 -> {
                viewModel.amount.value = stringBuilder.append("9").toString()
            }
            R.id.imageView2 -> {
                if (!TextUtils.isEmpty(amount)) {
                    if (amount.toDouble() != 0.0) {
                        var a = amount.substring(0, amount.length - 1)
                        if (TextUtils.isEmpty(a)) {
                            a = "0"
                        }
                        viewModel.amount.value = a
                    }
                }
            }
            R.id.textView15 -> {

            }
            R.id.textView14 -> {

            }
            R.id.textView12 -> {
                viewModel.amount.value = stringBuilder.append(".").toString()
            }
            R.id.textView13 -> {
                // TODO 验证数据有效性
                LogUtil.d(
                    "test083101",
                    "${viewModel.item?.id}, ${viewModel.amount.value}, ${viewModel.date}, ${viewModel.note.value}"
                )
//                noteCategoryId: Int, amount: Double, date: Date, information: String, accountId: Int
                val noteCategoryId = viewModel.item?.id ?: -1
                val amountDouble = amount.toDouble()
                val date = viewModel.date
                val information = viewModel.note.value
                val accountId = 0 // TODO
                if (viewModel.isNoteEntryValid(noteCategoryId, amountDouble, date, accountId)) {
                    viewModel.addNewNote(noteCategoryId, amountDouble, date, information, accountId)
                    activity?.finish()
                }
            }
            R.id.dateLayout -> {
                pvCustomTime1?.show()
            }
            else -> {
            }
        }
    }

    private var pvCustomTime1: TimePickerView? = null

    private fun initCustomTimePicker() {
        /**
         * 注意事项：
         * 1.自定义布局中，id为optionspicker或者timepicker的布局以及其子控件必须要有，否则会报空指针
         * 2.因为系统Calendar的月份是从0-11的，所以如果是调用Calendar的set方法来设置时间，月份的范围也要是从0-11
         * setRangDate方法控制起始终止时间(如果不设置范围，则使用默认时间1900-2100年)
         */
        val selectedDate = Calendar.getInstance() //系统当前时间
        val startDate = Calendar.getInstance()
        startDate.set(2018, 0, 1)
        val endDate = Calendar.getInstance()
        //时间选择器，自定义布局
        pvCustomTime1 = TimePickerBuilder(context) { date, v -> //选中事件回调
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
            viewModel.date = date
            binding.view11.dateTextView.text = format.format(viewModel.date)
            binding.view11.dateImageView.visibility = View.GONE
            Log.d("testDate", "date: $date")
        }
            .setContentTextSize(20)
            .setDate(selectedDate)
            .setRangDate(startDate, endDate)
            .setLayoutRes(R.layout.pickerview_custom_time) { v ->
                val tvSubmit = v.findViewById<TextView>(R.id.tv_finish)
                val tvCancel = v.findViewById<TextView>(R.id.tv_cancel)
                tvSubmit.setOnClickListener {
                    pvCustomTime1?.returnData()
                    pvCustomTime1?.dismiss()
                }
                tvCancel.setOnClickListener { pvCustomTime1?.dismiss() }
            }
            .setContentTextSize(20)
            .setType(booleanArrayOf(true, true, true, false, false, false))
            .setLabel("", "", "", "时", "分", "秒")
            .setLineSpacingMultiplier(1.5f)
            .setTextXOffset(40, 40, 40, 0, 0, 0)
            .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
            .setDividerColor(Color.parseColor("#e6e6e6"))
            .build()
    }
}