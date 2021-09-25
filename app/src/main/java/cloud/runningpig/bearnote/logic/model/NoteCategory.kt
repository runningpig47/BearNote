package cloud.runningpig.bearnote.logic.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 类别表
 * @param id 主键id
 * @param name 类别名称
 * @param icon 类别图标名
 * @param sort 类别分类：0代表支出，1代表收入
 * @param order 类别在分类中的顺序
 * @param uid 用户id 用户id TODO 外键：用户表主键id
 */
@Entity(tableName = "note_category")
data class NoteCategory(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var name: String,
    var icon: String,
    var sort: Int,
    var order: Int,
    var uid: Int,
)
