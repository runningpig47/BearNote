package cloud.runningpig.bearnote.logic.model

import androidx.room.ColumnInfo

data class ChartMonthBean(
    @ColumnInfo(name = "category_name") val categoryName: String,
    @ColumnInfo(name = "category_icon") val categoryIcon: String,
    @ColumnInfo(name = "count_category_id") val countCategoryId: Int,
    @ColumnInfo(name = "sum_note_amount") val sumNoteAmount: Double
)
