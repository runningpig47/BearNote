package cloud.runningpig.bearnote.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.databinding.DetailFragmentBinding
import cloud.runningpig.bearnote.ui.custom.CalendarBean
import cloud.runningpig.bearnote.ui.custom.CustomCalendarView
import com.google.android.material.tabs.TabLayoutMediator
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_PARAM1 = "test"

class DetailFragment : Fragment() {

    private var param1: Int? = null
    val viewModel by lazy {
        ViewModelProvider(this)[DetailViewModel::class.java]
    }
    private var _binding: DetailFragmentBinding? = null
    private val binding get() = _binding!!
    private val simpleDateFormat = SimpleDateFormat("M月d日", Locale.getDefault())

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
        val todayString = "${simpleDateFormat.format(calendarBean.date)}(${calendarBean.weekOfDay})"
        binding.dfTextView2.text = todayString
        binding.dfTextView2.setOnClickListener {
            binding.dfCustomCalendarView.goSelectedMonth()
        }
        binding.dfLinearLayout1.setOnClickListener {
            binding.dfCustomCalendarView.goLastMonth()
        }
        binding.dfLinearLayout2.setOnClickListener {
            binding.dfCustomCalendarView.goNextMonth()
        }
        binding.dfLinearLayout3.setOnClickListener {
            binding.dfCustomCalendarView.goThisMonth()
            binding.dfTextView2.text = todayString
        }
        binding.dfCustomCalendarView.setOnMonthChangerListener(object : CustomCalendarView.OnMonthChangerListener {
            override fun onMonthChanger(lastMonth: CalendarBean, newMonth: CalendarBean) {
                val newMonthDate = newMonth.date
                val simpleDateFormat = SimpleDateFormat("yyyy年M月", Locale.getDefault())
                val newMonthString: String = simpleDateFormat.format(newMonthDate)
                binding.dfTextView1.text = newMonthString
            }
        })
        binding.dfCustomCalendarView.setOnItemClickListener(object : CustomCalendarView.OnItemClickListener {
            override fun onItemClick(t: CalendarBean) {
                val s = "${simpleDateFormat.format(t.date)}(${t.weekOfDay})"
                binding.dfTextView2.text = s
                // TODO 请求记账&备忘数据
            }
        })
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

}