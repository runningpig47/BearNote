package cloud.runningpig.bearnote.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cloud.runningpig.bearnote.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

open class CustomCalendarView : FrameLayout {

    //    private val flipDistance: Int = ViewConfiguration.get(context).scaledTouchSlop
//    private val simpleDateFormat = SimpleDateFormat("yyyy年MM月", Locale.getDefault())
    private lateinit var listView: RecyclerView
    private val mDataList = ArrayList<CalendarBean>()
    private lateinit var mAdapter: BaseRecyclerAdapter<CalendarBean>
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // 当前显示月份，这个值是当前页面展示中的月份
    private lateinit var mCurrentMonth: CalendarBean

    // 软件打开的当天，这个值赋值后不会改变
    private lateinit var mCurrentDay: CalendarBean

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        // 初始化当天日期
        val instanceToday = Calendar.getInstance()
        val day = instanceToday.get(Calendar.DAY_OF_MONTH).toString()
        val dayOfWeek = getWeekString(instanceToday.get(Calendar.DAY_OF_WEEK))
        mCurrentDay = CalendarBean(0, dayOfWeek, day, instanceToday.time)
        val view = LayoutInflater.from(context).inflate(R.layout.view_calendar, this)
        listView = view.findViewById(R.id.list_date)
        initAdapter()
        initData(null)
        listView.layoutManager = GridLayoutManager(context, 7)
        listView.adapter = mAdapter
    }

    private fun initAdapter() {
        mAdapter = object :
            BaseRecyclerAdapter<CalendarBean>(context, mDataList, R.layout.item_scheduling_day) {

            override fun onBindViewHolder(
                viewHolder: ViewHolder,
                itemVO: CalendarBean,
                mSelectedItem: CalendarBean?,
                position: Int
            ) {
                val isdTextView = viewHolder.findViewById<TextView>(R.id.isd_textView)
                val isdTextView2 = viewHolder.findViewById<TextView>(R.id.isd_textView2)
                val isdImageView = viewHolder.findViewById<ImageView>(R.id.isd_imageView)
                isdTextView.text = itemVO.day
                if (itemVO.dayType == 0) { // this month
                    if (isdTextView2.visibility != VISIBLE) {
                        isdTextView2.visibility = VISIBLE
                    }
                    if (isdImageView.visibility != VISIBLE) {
                        isdImageView.visibility = VISIBLE
                    }
                    // 判断选中的日期
                    val itemVOString: String = simpleDateFormat.format(itemVO.date)
                    val mSelectedItemString: String = simpleDateFormat.format(mSelectedItem?.date ?: "")
                    if (itemVOString == mSelectedItemString) {
                        isdTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
                        isdTextView.setBackgroundResource(R.color.color_DD3e332d)
                        viewHolder.getRootView().setBackgroundResource(R.drawable.rec_red)
                    } else {
                        isdTextView.setTextColor(ContextCompat.getColor(context, R.color.color_4f4f4f))
                        isdTextView.setBackgroundResource(R.color.white)
                        viewHolder.getRootView().setBackgroundResource(android.R.color.transparent)
                    }
                } else {
                    // 其他月份
                    isdTextView.setTextColor(ContextCompat.getColor(context, R.color.color_bababa))
                    isdTextView.setBackgroundResource(R.color.white)
                    viewHolder.getRootView().setBackgroundResource(android.R.color.transparent)
                    if (isdTextView2.visibility != INVISIBLE) {
                        isdTextView2.visibility = INVISIBLE
                    }
                    if (isdImageView.visibility != INVISIBLE) {
                        isdImageView.visibility = INVISIBLE
                    }
                }
            }
        }
        mAdapter.setSelectedItem(mCurrentDay)
        mAdapter.setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener<CalendarBean> {
            override fun onItemClick(t: CalendarBean, position: Int) {
                if (t.dayType == 0) {
                    itemClickListener?.onItemClick(t)
                    mAdapter.setSelectedItem(t)
                }
            }
        })
    }

//    private var downX = 0F
//    private var downY = 0F

//    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
//        when (event?.action) {
//            MotionEvent.ACTION_DOWN -> {
//                downX = event.x
//                downY = event.y
//            }
//            MotionEvent.ACTION_UP -> {
//                val upX = event.x
//                val upY = event.y
//                val speedY = abs(downY - upY)
//                if (upX - downX > speedY && upX - downX > flipDistance) {
//                    goLastMonth()
//                } else if (downX - upX > speedY && downX - upX > flipDistance) {
//                    goNextMonth()
//                }
//            }
//        }
//        return super.dispatchTouchEvent(event)
//    }

    /** 显示上一个月（以基准月为起点） */
    fun goLastMonth() {
//        val lastMonth: String = simpleDateFormat.format(mCurrentMonthCalendar.date)
        val lastMonth = mCurrentMonth
        val instance: Calendar = Calendar.getInstance()
//        instance.clear()
        instance.time = mCurrentMonth.date
        instance.add(Calendar.MONTH, -1)
        val tempBean = CalendarBean()
        tempBean.date = instance.time // 上月的这一天Date
        initData(tempBean) // 以上月作为基准传进去
        listener?.onMonthChanger(lastMonth, tempBean)
    }

    /** 显示下一个月（以基准月为起点） */
    fun goNextMonth() {
        val lastMonth = mCurrentMonth
        val instance: Calendar = Calendar.getInstance()
        instance.time = mCurrentMonth.date
        instance.add(Calendar.MONTH, 1)
        val tempBean = CalendarBean()
        tempBean.date = instance.time
        initData(tempBean) // 以下月作为基准传进去
        listener?.onMonthChanger(lastMonth, tempBean)
    }

    /** 加载打开软件的当月数据 */
    fun goThisMonth() {
        mAdapter.setSelectedItem(mCurrentDay)
        initData(mCurrentDay)
        listener?.onMonthChanger(mCurrentMonth, mCurrentDay)
    }

    fun goSelectedMonth() {
        val item = mAdapter.getSelectedItem()
        initData(item)
        listener?.onMonthChanger(mCurrentMonth, item!!)
    }

    /**
     * 以参数中的的Date为当前月基准，遍历当前月的每一天，最后拼接上月和下月的天，
     * 最终拼接好需要展示的天会存放到mDataList中，并通过adapter通知list更新
     * @param indexDateBean 存储指定的current Date
     */
    private fun initData(indexDateBean: CalendarBean?) {
        mDataList.clear()
        val instance: Calendar = Calendar.getInstance()
        if (indexDateBean != null) {
            // 自定月份
            instance.time = indexDateBean.date
        }
        // 获取当前月最大日期（目前这里的当前月，指上个月）
        val maxDate: Int = instance.getActualMaximum(Calendar.DATE)
        instance.set(Calendar.DAY_OF_MONTH, 1)
        // 获取当前月所有的天数（目前遍历了上月的每一天）
        for (i in 0 until maxDate) {
            val day = instance.get(Calendar.DAY_OF_MONTH)
            val dayOfWeek = instance.get(Calendar.DAY_OF_WEEK)
            val calendarBean = CalendarBean()
            calendarBean.day = day.toString()
            calendarBean.date = instance.time
            calendarBean.weekOfDay = getWeekString(dayOfWeek)
            mDataList.add(calendarBean)
            instance.add(Calendar.DATE, 1)
        }
        // 赋值当前月份，传入当前月的第一天bean
        mCurrentMonth = mDataList[0]
        val calendarBeanList = processDay(mDataList) // 拼接上月+当前月+下月
        mDataList.clear()
        mDataList.addAll(calendarBeanList)
//        mDataList.forEach {
//            Log.d("testCalendar", "$it")
//        }
        mAdapter.notifyDataSetChanged()
    }

    /**
     * 拼接上月+当前月+下月
     * @param currentMonthList 存放当前基准月所有天的list
     * @return 返回拼接上月+当前月+下月天的完整list
     * */
    private fun processDay(currentMonthList: ArrayList<CalendarBean>): ArrayList<CalendarBean> {
        val list = ArrayList<CalendarBean>()
        // 获取上一个月需要补全的天数
        val lastMonthList: ArrayList<CalendarBean> = getLastMonth(currentMonthList[0])
        // 获取下一个月需要补全的天数
        val nextMonthList: ArrayList<CalendarBean> = getNextMonth(currentMonthList[0])
        list.addAll(lastMonthList)
        list.addAll(currentMonthList)
        list.addAll(nextMonthList)
        return list
    }

    /**
     * 以参数中的Date为基准，查询界面需要显示的上月的天
     * @param calendarBean 基准月份的Date
     * @return 基准Date上月天的list
     */
    private fun getLastMonth(calendarBean: CalendarBean): ArrayList<CalendarBean> {
        // 获取当前月第一天
        val calendarFirstDay = Calendar.getInstance()
        calendarFirstDay.clear()
        calendarFirstDay.time = calendarBean.date
        calendarFirstDay.set(Calendar.DAY_OF_MONTH, 1)
        // 当月第一天是星期几
        val firstDayOfWee = calendarFirstDay.get(Calendar.DAY_OF_WEEK)
        // firstDayOfWeek=1时，就是星期日，当前月第一天已经处于星期日，不需要添加上一个月补充天数
        if (firstDayOfWee != 1) {
            // 需要补全的天数，假设needAdd为6时，说明是星期六，需要补6天（一周的第一天是周日）
            val needAdd: Int = firstDayOfWee - 1
            val lastMonthList = ArrayList<CalendarBean>()
            for (i in 1..needAdd) {
                val dayBean = CalendarBean()
                dayBean.dayType = 1
                dayBean.weekOfDay = getWeekString(needAdd - i + 1)
                // 取出上一个月需要补全的天数
                calendarFirstDay.add(Calendar.DATE, -1) // 倒着往前加，添加时用index=0正序
                val lastMonthDay = calendarFirstDay.get(Calendar.DAY_OF_MONTH)
                dayBean.day = lastMonthDay.toString()
                dayBean.date = calendarFirstDay.time
                lastMonthList.add(0, dayBean)
            }
            return lastMonthList
        }
        return ArrayList()
    }

    private fun getNextMonth(calendarBean: CalendarBean): ArrayList<CalendarBean> {
        // 获取当前月最后一天
        val calendarLastDay = Calendar.getInstance()
        calendarLastDay.clear()
        calendarLastDay.time = calendarBean.date
        calendarLastDay.add(Calendar.MONTH, 1)
        calendarLastDay.set(Calendar.DAY_OF_MONTH, 0) // 这里设置DAY为0确实可以到达上个月最后一天，即当前月最后一天
        // 获取当前月最后一天是星期几
        val nextDayOfWeek = calendarLastDay.get(Calendar.DAY_OF_WEEK)
        if (nextDayOfWeek != 7) {
            // nextDayOfWeek=7时，就是星期六，当前月最后一天已经处于星期六，不需要添加下一个月补充天数
            val needAdd = 7 - nextDayOfWeek
            val nextMonthList = ArrayList<CalendarBean>()
            for (i in 1..needAdd) {
                val dayBean = CalendarBean()
                dayBean.dayType = 2 // 2: 下月
                dayBean.weekOfDay = getWeekString(nextDayOfWeek + i) // TODO 我认为是这样的，一会儿验证
                // 取出下一个月需要补全的天数
                calendarLastDay.add(Calendar.DATE, 1)
                // 天数
                val nextMonthDay = calendarLastDay.get(Calendar.DAY_OF_MONTH)
                dayBean.day = nextMonthDay.toString()
                dayBean.date = calendarLastDay.time
                nextMonthList.add(dayBean)
            }
            return nextMonthList
        }
        return ArrayList()
    }

    private fun getWeekString(i: Int): String {
        return when (i) {
            1 -> "星期日"
            2 -> "星期一"
            3 -> "星期二"
            4 -> "星期三"
            5 -> "星期四"
            6 -> "星期五"
            7 -> "星期六"
            else -> "null"
        }
    }

    private var listener: OnMonthChangerListener? = null

    interface OnMonthChangerListener {
        fun onMonthChanger(lastMonth: CalendarBean, newMonth: CalendarBean)
    }

    fun setOnMonthChangerListener(listener: OnMonthChangerListener) {
        this.listener = listener
    }

    fun getCurrentDay(): CalendarBean = mCurrentDay

    private var itemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(t: CalendarBean)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

}