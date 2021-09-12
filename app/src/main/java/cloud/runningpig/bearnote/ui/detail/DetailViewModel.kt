package cloud.runningpig.bearnote.ui.detail

import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import cloud.runningpig.bearnote.BearNoteRepository
import cloud.runningpig.bearnote.logic.model.ChartMonthBean
import cloud.runningpig.bearnote.logic.model.DailyAmount
import cloud.runningpig.bearnote.logic.model.NoteDetail
import com.github.mikephil.charting.utils.ColorTemplate
import java.util.*
import kotlin.collections.ArrayList

class DetailViewModel(private val bearNoteRepository: BearNoteRepository) : ViewModel() {
    val date = MutableLiveData(Date())

    val queryByDate: LiveData<List<NoteDetail>> = Transformations.switchMap(date) {
        val startOfDay = getStartOfDay(it)
        val endOfDay = getEndOfDay(it)
        bearNoteRepository.queryByDate(startOfDay, endOfDay).asLiveData()
    }

    val page = MutableLiveData(0)

    // TODO 图表按钮切换月份
    val month = MutableLiveData(Date())
    val dataList = ArrayList<ChartMonthBean>()

    // 月内所有记账的总金额
    var amount: Double = 0.0

    // 月内前5记账的总金额
    var topFiveAmount = 0.0

    // 月内其他记账笔数（除去前5）
    var otherCountCategoryId = 0

    fun queryByMonth(sort: Int): LiveData<List<ChartMonthBean>> = Transformations.switchMap(month) {
        val startOfMonth = getStartOfMonth(it)
        val endOfMonth = getEndOfMonth(it)
        bearNoteRepository.queryByMonth(sort, startOfMonth, endOfMonth).asLiveData()
    }

    val map = HashMap<LiveData<List<DailyAmount>>, Observer<List<DailyAmount>>>()

    fun queryDailyAmount(date: Date): LiveData<List<DailyAmount>> {
        val startOfDay = getStartOfDay(date)
        val endOfDay = getEndOfDay(date)
        return bearNoteRepository.queryDailyAmount(startOfDay, endOfDay)
    }

    /** 设置参数Date到当月第一天00:00:00 */
    private fun getStartOfMonth(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    /** 设置参数Date到当月最后一天23:59:59 */
    private fun getEndOfMonth(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MONTH, 1)
        calendar.set(Calendar.DAY_OF_MONTH, 0) // 这里设置DAY为0确实可以到达上个月最后一天，即当前月最后一天
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.time
    }

    /** 设置参数Date到当天00:00:00 */
    private fun getStartOfDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    /** 设置参数Date到当天23:59:59 */
    private fun getEndOfDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.time
    }

    fun generateCenterSpannableText(): SpannableString {
        val s = SpannableString("BearNote\n小熊记账")
        s.setSpan(RelativeSizeSpan(1.2f), 0, 8, 0)
        s.setSpan(StyleSpan(Typeface.NORMAL), 8, s.length, 0)
        s.setSpan(ForegroundColorSpan(Color.GRAY), 8, s.length, 0)
        s.setSpan(RelativeSizeSpan(1.1f), 8, s.length, 0)
//        s.setSpan(StyleSpan(Typeface.ITALIC), 8, s.length, 0)
        s.setSpan(ForegroundColorSpan(ColorTemplate.getHoloBlue()), 8, s.length, 0)
        return s
    }

    fun generateDescriptionText(sort: Int): String {
        val sortString = if (sort == 0) "支出" else "收入"
        return "${sortString}类别排行"
    }

}

class DetailViewModelFactory(private val bearNoteRepository: BearNoteRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(bearNoteRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}