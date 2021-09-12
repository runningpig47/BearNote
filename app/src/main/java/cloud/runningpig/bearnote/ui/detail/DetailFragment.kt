package cloud.runningpig.bearnote.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import cloud.runningpig.bearnote.BearNoteApplication
import cloud.runningpig.bearnote.BearNoteRepository
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.databinding.DetailFragmentBinding
import cloud.runningpig.bearnote.logic.dao.BearNoteDatabase
import cloud.runningpig.bearnote.logic.model.DailyAmount
import cloud.runningpig.bearnote.logic.utils.LogUtil
import cloud.runningpig.bearnote.ui.custom.BaseRecyclerAdapter
import cloud.runningpig.bearnote.ui.custom.CalendarBean
import cloud.runningpig.bearnote.ui.custom.CustomCalendarView
import com.google.android.material.tabs.TabLayoutMediator
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_PARAM1 = "test"

class DetailFragment : Fragment() {

    private var param1: Int? = null

    private var _binding: DetailFragmentBinding? = null
    private val binding get() = _binding!!

    private val simpleDateFormat = SimpleDateFormat("M月d日", Locale.getDefault())
    private val simpleDateFormat2 = SimpleDateFormat("yyyy年M月", Locale.getDefault())

    val viewModel: DetailViewModel by activityViewModels {
        val repository = BearNoteRepository.getInstance(
            BearNoteDatabase.getDatabase(BearNoteApplication.context).noteCategoryDao(),
            BearNoteDatabase.getDatabase(BearNoteApplication.context).noteDao()
        )
        DetailViewModelFactory(repository)
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
        _binding = DetailFragmentBinding.inflate(inflater, container, false)
        binding.dfViewPager2.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2

            override fun createFragment(position: Int): Fragment = when (position) {
                0 -> NoteListFragment.newInstance("", "")
                else -> InformationListFragment.newInstance("", "")
            }
        }
        TabLayoutMediator(binding.dfTabLayout, binding.dfViewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.note)
                else -> tab.text = getString(R.string.information)
            }
        }.attach()
        // 处理日历相关
        val calendarBean = binding.dfCustomCalendarView.getCurrentDay()
        binding.dfTextView1.text = simpleDateFormat2.format(calendarBean.date)
        val todayString = "${simpleDateFormat.format(calendarBean.date)}(${calendarBean.weekOfDay})"
        binding.dfTextView2.text = todayString
        binding.dfTextView2.setOnClickListener {
            binding.dfCustomCalendarView.goSelectedDay()
        }
        binding.dfLinearLayout1.setOnClickListener {
            binding.dfCustomCalendarView.goLastMonth()
        }
        binding.dfLinearLayout2.setOnClickListener {
            binding.dfCustomCalendarView.goNextMonth()
        }
        binding.dfLinearLayout3.setOnClickListener {
            binding.dfCustomCalendarView.goToday()
            binding.dfTextView2.text = todayString
            viewModel.date.value = Date()
        }
        binding.dfCustomCalendarView.setOnMonthChangerListener(object : CustomCalendarView.OnMonthChangerListener {
            override fun onMonthChanger(lastMonth: CalendarBean, newMonth: CalendarBean) {
                val newMonthDate = newMonth.date
                val newMonthString: String = simpleDateFormat2.format(newMonthDate)
                binding.dfTextView1.text = newMonthString
            }
        })
        binding.dfCustomCalendarView.setOnItemClickListener(object : CustomCalendarView.OnItemClickListener {
            override fun onItemClick(t: CalendarBean) {
                val s = "${simpleDateFormat.format(t.date)}(${t.weekOfDay})"
                binding.dfTextView2.text = s
                viewModel.date.value = t.date
            }
        })
        binding.dfCustomCalendarView.setOnSubscribeListener(object : CustomCalendarView.OnSubscribeListener {
            override fun subscribe(mAdapter: BaseRecyclerAdapter<CalendarBean>, mDataList: ArrayList<CalendarBean>) {
                for (i in 0 until mDataList.size) {
                    if (mDataList[i].dayType == 0) { // 只订阅当月日历
                        val date = mDataList[i].date
                        val liveData = viewModel.queryDailyAmount(date)
                        val observer = Observer<List<DailyAmount>> {
                            if (it.isNotEmpty()) {
                                mDataList[i].dailyAmount = it
                                mAdapter.notifyItemChanged(i)
                                LogUtil.d("test2021091201", "订阅非空: position: $i, $it")
                            }
                        }
                        liveData.observeForever(observer)
                        viewModel.map[liveData] = observer
                    }
                }
            }
        })
        binding.dfCustomCalendarView.setUnsubscribeListener(object : CustomCalendarView.UnsubscribeListener {
            override fun unsubscribe() {
                for ((key, value) in viewModel.map) {
                    key.removeObserver(value)
                    LogUtil.d("test2021091201", "${key.hasObservers()}, ${key.hasActiveObservers()}")
                }
                viewModel.map.clear()
            }
        })
        binding.dfCustomCalendarView.initData(null)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int) =
            DetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        for ((key, value) in viewModel.map) {
            key.removeObserver(value)
            if (key.hasObservers()) {
                throw RuntimeException("在Destroy时监听存在")
            }
        }
        viewModel.map.clear()
    }

}