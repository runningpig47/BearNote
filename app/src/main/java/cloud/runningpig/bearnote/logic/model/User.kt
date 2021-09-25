package cloud.runningpig.bearnote.logic.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * 用户表
 * @param id 用户id
 * @param userName 昵称
 */
@Entity(tableName = "user")
class User(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "user_name") var userName: String,
    // 服务器设置password字段
)