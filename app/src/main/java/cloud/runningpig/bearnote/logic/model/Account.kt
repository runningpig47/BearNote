package cloud.runningpig.bearnote.logic.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 账户表
 * @param id 主键id
 * @param name 账户名称
 * @param icon 账户图标名
 * @param order 账户的顺序
 * @param uid 用户id 用户id TODO 外键：用户表主键id
 * @param recorded 是否存在该账户的记账数据
 * @param balance 账户余额
 * @param isUpload 是否已上传到服务器
 */
@Entity(tableName = "account")
data class Account(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var name: String,
    var icon: String,
    var order: Int,
    var uid: Int,
    var recorded: Boolean,
    var balance: Double,
    @ColumnInfo(name = "is_upload") var isUpload: Int
)