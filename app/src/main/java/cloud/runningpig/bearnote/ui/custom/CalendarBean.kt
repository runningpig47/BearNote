package cloud.runningpig.bearnote.ui.custom

import cloud.runningpig.bearnote.logic.model.DailyAmount
import java.util.*

data class CalendarBean(
    // 0：当月；1：上月；2：下月
    var dayType: Int = 0,
    // 星期
    var weekOfDay: String = "",
    // 日期
    var day: String = "",
    // 实际日期
    var date: Date = Date()
) {
    var dailyAmount: List<DailyAmount?>? = null
}