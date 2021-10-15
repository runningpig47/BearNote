package cloud.runningpig.bearnote.ui.assets

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import cloud.runningpig.bearnote.BearNoteApplication
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.databinding.ActivityTransferBinding
import cloud.runningpig.bearnote.logic.BearNoteRepository
import cloud.runningpig.bearnote.logic.dao.BearNoteDatabase
import cloud.runningpig.bearnote.logic.model.IconMap
import cloud.runningpig.bearnote.ui.BaseActivity
import cloud.runningpig.bearnote.ui.detail.DetailViewModel
import cloud.runningpig.bearnote.ui.detail.DetailViewModelFactory
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.view.TimePickerView
import java.text.SimpleDateFormat
import java.util.*

class TransferActivity : BaseActivity() {

    private var _binding: ActivityTransferBinding? = null
    private val binding get() = _binding!!

    val viewModel: DetailViewModel by viewModels {
        val repository = BearNoteRepository.getInstance(
            BearNoteDatabase.getDatabase(BearNoteApplication.context).noteCategoryDao(),
            BearNoteDatabase.getDatabase(BearNoteApplication.context).noteDao()
        )
        DetailViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTransferBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.taTitleLayout.findViewById<TextView>(R.id.title_textView).text = "转账"
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        binding.taTextView3.text = format.format(viewModel.mDate)
        initCustomTimePicker()
        viewModel.loadAccount().observe(this) {
            viewModel.accountList = it
        }
        binding.linearLayout1.setOnClickListener {
            val saDialogFragment = SADialogFragment()
            saDialogFragment.setOnBindViewListener(object : SADialogFragment.OnBindViewListener {
                override fun bindView(view: View) {
                    view.findViewById<TextView>(R.id.sad_textView1).text = "选择转出账户"
                    val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
                    val adapter = SADialogList1Adapter { item, _ ->
                        binding.taImageView1.setImageResource(IconMap.map2[item.icon] ?: R.drawable.ic_error)
                        binding.taTextView1.text = item.name
                        viewModel.from = item
                        saDialogFragment.dismiss()
                    }
                    recyclerView.adapter = adapter
                    adapter.submitList(null)
                    adapter.submitList(viewModel.accountList)
                }
            })
            val manager = supportFragmentManager
            val transaction = manager.beginTransaction()
            transaction.add(saDialogFragment, "")
            transaction.commitAllowingStateLoss()
        }
        binding.linearLayout2.setOnClickListener {
            val saDialogFragment = SADialogFragment()
            saDialogFragment.setOnBindViewListener(object : SADialogFragment.OnBindViewListener {
                override fun bindView(view: View) {
                    view.findViewById<TextView>(R.id.sad_textView1).text = "选择转入账户"
                    val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
                    val adapter = SADialogList1Adapter { item, _ ->
                        binding.taImageView2.setImageResource(IconMap.map2[item.icon] ?: R.drawable.ic_error)
                        binding.taTextView2.text = item.name
                        viewModel.to = item
                        saDialogFragment.dismiss()
                    }
                    recyclerView.adapter = adapter
                    adapter.submitList(null)
                    adapter.submitList(viewModel.accountList)
                }
            })
            val manager = supportFragmentManager
            val transaction = manager.beginTransaction()
            transaction.add(saDialogFragment, "")
            transaction.commitAllowingStateLoss()
        }
        binding.linearLayout5.setOnClickListener {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            pvCustomTime1?.show()
        }
        binding.saveAction.setOnClickListener {
            if (transferEntryValid()) { // 前置检验要保证from和to非空
                // 1. 更新账户表from和to转入转出金额
                val fromBalance = viewModel.from!!.balance
                val toBalance = viewModel.to!!.balance
                val amount = binding.taEditText1.text.toString().toDouble()
                viewModel.from!!.balance = fromBalance - amount
                viewModel.to!!.balance = toBalance + amount
                viewModel.updateList2(listOf(viewModel.from!!, viewModel.to!!))
                // 2. 转账写入转账记录表
                val info = binding.taEditText2.text.toString()
                viewModel.addNewTransfer(viewModel.from!!.id, viewModel.to!!.id, amount, info, viewModel.mDate)
                finish()
            }
            // 清空
            viewModel.from = null
            viewModel.to = null
        }
    }

    private fun transferEntryValid(): Boolean {
        return viewModel.transferEntryValid(
            viewModel.from?.id,
            viewModel.to?.id,
            binding.taEditText1.text.toString(),
        )
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
        pvCustomTime1 = TimePickerBuilder(this) { date, _ -> //选中事件回调
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
            binding.taTextView3.text = format.format(date)
            viewModel.mDate = date
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