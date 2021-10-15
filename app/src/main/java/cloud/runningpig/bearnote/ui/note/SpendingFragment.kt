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
import androidx.recyclerview.widget.RecyclerView
import cloud.runningpig.bearnote.BearNoteApplication
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.databinding.SpendingFragmentBinding
import cloud.runningpig.bearnote.logic.BearNoteRepository
import cloud.runningpig.bearnote.logic.dao.BearNoteDatabase
import cloud.runningpig.bearnote.logic.model.IconMap
import cloud.runningpig.bearnote.logic.model.NoteCategory
import cloud.runningpig.bearnote.logic.model.NoteDetail
import cloud.runningpig.bearnote.logic.utils.Injector
import cloud.runningpig.bearnote.logic.utils.LogUtil
import cloud.runningpig.bearnote.logic.utils.ViewUtil
import cloud.runningpig.bearnote.logic.utils.showToast
import cloud.runningpig.bearnote.ui.assets.SADialogFragment
import cloud.runningpig.bearnote.ui.assets.SADialogList1Adapter
import cloud.runningpig.bearnote.ui.custom.InputDialogFragment
import cloud.runningpig.bearnote.ui.detail.DetailViewModel
import cloud.runningpig.bearnote.ui.detail.DetailViewModelFactory
import cloud.runningpig.bearnote.ui.note.category.CategoryActivity
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.view.TimePickerView
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_PARAM1 = "sort"

/**
 * 支出&收入共用
 */
class SpendingFragment : Fragment(), View.OnClickListener {
    private var sort: Int? = null

    private val viewModel: SpendingViewModel by activityViewModels {
        Injector.providerSpendingViewModelFactory(requireContext())
    }

    val viewModel2: DetailViewModel by activityViewModels {
        val repository = BearNoteRepository.getInstance(
            BearNoteDatabase.getDatabase(BearNoteApplication.context).noteCategoryDao(),
            BearNoteDatabase.getDatabase(BearNoteApplication.context).noteDao()
        )
        DetailViewModelFactory(repository)
    }

    private var _binding: SpendingFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            sort = it.getInt(ARG_PARAM1, 0)
        }
        viewModel2.loadAccount().observe(this) {
            viewModel2.accountList = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = SpendingFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ItemList1Adapter { position, item ->
            if (position != -1) {
                viewModel.categoryId = item!!.id
                recyclerViewUp()
                binding.spendingRecyclerView.scrollToPosition(position)
            } else {
                val intent = Intent(context, CategoryActivity::class.java)
                intent.putExtra("page", sort)
                context?.startActivity(intent)
            }
        }
        binding.spendingRecyclerView.adapter = adapter
        if (viewModel.noteId >= 0) { // 修改记账
            MainScope().launch(Dispatchers.IO) {
                viewModel2.queryById2(viewModel.noteId).collect { noteDetail: NoteDetail? ->
                    if (noteDetail != null) {
                        if (noteDetail.accountId != -1) {
                            val account = viewModel2.queryByAid3(noteDetail.accountId)
                            withContext(Dispatchers.Main) {
                                viewModel.oldAccountItem = account
                                viewModel.accountItem.value = account
                            }
                        }
                        withContext(Dispatchers.Main) {
                            if (isAdded) {
                                recyclerViewUp()
                            }
                            viewModel.categoryId = noteDetail.categoryId
                            if (noteDetail.categorySort == sort) {
                                adapter.setSelectedItemId(viewModel.categoryId)
                                adapter.notifyDataSetChanged()
                            }
                            viewModel.amount.value = noteDetail.noteAmount.toString()
                            viewModel.oldAmount = noteDetail.noteAmount.toString()
                            viewModel.date = noteDetail.noteDate
                            val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
                            binding.view11.dateTextView.text = format.format(viewModel.date)
                            binding.view11.dateImageView.visibility = View.GONE
                            viewModel.info.value = noteDetail.information
                        }
                    }
                }
            }
            // 更新过程中记得删除类别后，更新viewmodel中的类别id为-1
        }
        viewModel.loadBySort(sort ?: 0).observe(this.viewLifecycleOwner) {
            val list = LinkedList(it)
            val setting = NoteCategory(
                // 添加末尾固定的设置按钮
                name = "设置",
                icon = "setting",
                sort = 0,
                order = Int.MAX_VALUE,
                uid = 0
            )
            list.add(setting)
            adapter.submitList(list)
            // TODO 是否需要修改观察的page字段？
            val set = HashSet<Int>()
            LogUtil.d("test111", "created set: $set")
            list.forEach { noteCategory ->
                set.add(noteCategory.id)
            }
            LogUtil.d("test111", "added set: $set\n adapter.getId: ${adapter.getSelectedItemId()}")
            // 设置-删除所有类别后 OR 删除已经选中的类别后，回来键盘应该是关闭的
            if (it.isEmpty() || !set.contains(adapter.getSelectedItemId())) {
                recyclerViewDown()
                // 判断在更新的情况下，正在更新的类别id是否已经被删除。如果已经被删除，应该讲noteId设置为-1，即新增记账而不是更新记账了
                if (viewModel.noteId > 0 && viewModel.categoryId > 0) {
                    if (viewModel.categorySort == sort) {
                        if (!set.contains(viewModel.categoryId)) {
                            viewModel.noteId = -1
                            viewModel.categoryId = -1 // 多余的
                        }
                    }
                }
            }
        }
        viewModel.page.observe(this.viewLifecycleOwner) {
            recyclerViewDown()
            adapter.setSelectedItemId(-1)
            adapter.notifyDataSetChanged() // 清除选中背景
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
        viewModel.info.observe(this.viewLifecycleOwner) {
            binding.view11.view12.inputEditText.setText(it)
        }
        viewModel.accountItem.observe(this.viewLifecycleOwner) { account ->
            if (account == null) {
                binding.view11.view12.inputImageView.setImageResource(R.drawable.ic_wallet)
            } else {
                binding.view11.view12.inputImageView.setImageResource(IconMap.map2[account.icon] ?: R.drawable.ic_error)
            }
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
        binding.view11.view12.inputImageView.setOnClickListener(this) // 选择账户
        initCustomTimePicker()

        MainScope().launch {
            delay(2000)
            if (sort == viewModel.page.value) {
                Log.d("test20211015", "noteId: ${viewModel.noteId}")
                Log.d("test20211015", "categoryId: ${viewModel.categoryId}")
                Log.d("test20211015", "categorySort: ${viewModel.categorySort}")
                Log.d("test20211015", "accountItem: ${viewModel.accountItem.value}")
                Log.d("test20211015", "date: ${viewModel.date}")
                Log.d("test20211015", "amount: ${viewModel.amount.value}")
                Log.d("test20211015", "info: ${viewModel.info.value}")
                Log.d("test20211015", "page: ${viewModel.page.value}")
                Log.d("test20211015", "oldAccountItem: ${viewModel.oldAccountItem}")
                Log.d("test20211015", "oldAmount: ${viewModel.oldAmount}")
            }
        }
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
//                var amount = viewModel.amount.value
                val date = viewModel.date
                val info = viewModel.info.value
                val accountId = viewModel.accountItem.value?.id ?: -1
                if (viewModel.isNoteEntryValid(viewModel.categoryId, amount)) {
                    MainScope().launch {
                        if (viewModel.noteId >= 0) { // 更新记账
                            // 1. 检查是否有账户，如果有账户，将旧的记账金额逆向写回
                            // 2. 然后按新记账的方式处理
                            viewModel.oldAccountItem?.let { // TODO 事务
                                val oldAmount = viewModel.oldAmount.toDouble()
                                var balance = it.balance
                                if (sort == 0) {
                                    balance -= oldAmount
                                } else {
                                    balance += oldAmount
                                }
                                it.balance = balance
                                viewModel2.update2(it)
                            }
                        }
                        // 普通记账：1.更新账户记录 2.写入记账明细记录
                        val amountDouble = amount.toDouble()
                        viewModel.accountItem.value?.let {
                            var balance = it.balance
                            if (sort == 0) {
                                balance -= amountDouble
                            } else {
                                balance += amountDouble
                            }
                            it.balance = balance
                            viewModel2.update2(it)
                        }
                        if (viewModel.noteId >= 0) { // 更新记账
                            viewModel.updateNote(amount.toDouble(), date, info, accountId)
                        } else {
                            viewModel.addNewNote(amount.toDouble(), date, info, accountId)
                        }
                    }
                    Log.d("test20211015", "noteId: ${viewModel.noteId}")
                    Log.d("test20211015", "categoryId: ${viewModel.categoryId}")
                    Log.d("test20211015", "categorySort: ${viewModel.categorySort}")
                    Log.d("test20211015", "accountItem: ${viewModel.accountItem.value}")
                    Log.d("test20211015", "date: ${viewModel.date}")
                    Log.d("test20211015", "amount: ${viewModel.amount.value}")
                    Log.d("test20211015", "info: ${viewModel.info.value}")
                    Log.d("test20211015", "page: ${viewModel.page.value}")
                    Log.d("test20211015", "oldAccountItem: ${viewModel.oldAccountItem}")
                    Log.d("test20211015", "oldAmount: ${viewModel.oldAmount}")
                    activity?.finish()
                } else {
                    "输入错误".showToast()
                }
            }
            R.id.dateLayout -> {
                pvCustomTime1?.show()
            }
            R.id.input_imageView -> { // 选择账户
                if (viewModel2.accountList.isNotEmpty()) {
                    val saDialogFragment = SADialogFragment()
                    saDialogFragment.setOnBindViewListener(object : SADialogFragment.OnBindViewListener {
                        override fun bindView(view: View) {
                            view.findViewById<TextView>(R.id.sad_textView1).text = "选择账户"
                            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
                            val adapter = SADialogList1Adapter { item, _ ->
                                viewModel.accountItem.value = item
                                saDialogFragment.dismiss()
                            }
                            recyclerView.adapter = adapter
                            adapter.submitList(null)
                            adapter.submitList(viewModel2.accountList)
                        }
                    })
                    val manager = childFragmentManager
                    val transaction = manager.beginTransaction()
                    transaction.add(saDialogFragment, "")
                    transaction.commitAllowingStateLoss()
                } else {
                    "账户为空".showToast()
                }
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
        pvCustomTime1 = TimePickerBuilder(context) { date, _ -> //选中事件回调
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
            viewModel.date = date
            binding.view11.dateTextView.text = format.format(viewModel.date)
            binding.view11.dateImageView.visibility = View.GONE
            LogUtil.d("testDate", "date: $date")
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