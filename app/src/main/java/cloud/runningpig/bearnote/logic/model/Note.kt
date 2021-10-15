package cloud.runningpig.bearnote.logic.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * 记账表
 * @param id 主键id
 * @param noteCategoryId 类别id TODO 外键：NoteCategory表主键id
 * @param amount 记账金额
 * @param date 记账日期&时间
 * @param information 备注信息
 * @param uid 用户id TODO 外键：用户表主键id
 * @param accountId 账户id TODO 外键：账户表主键id
 * @param isUpload 是否已上传到服务器
 */
@Entity(tableName = "note")
data class Note(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "note_category_id") var noteCategoryId: Int,
    var amount: Double,
    var date: Date,
    var information: String?,
    @ColumnInfo(name = "account_id") var accountId: Int,
    var uid: Int
)