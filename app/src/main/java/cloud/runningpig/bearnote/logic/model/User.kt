package cloud.runningpig.bearnote.logic.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * 用户表
 * @param id 用户id
 * @param nickname 昵称
 * @param gender 性别
 * @param registrationDate 注册日期
 * @param password 密码
 */
@Entity(tableName = "user")
class User(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "nick_name") var nickname: String,
    var sex: Boolean,
    @ColumnInfo(name = "registration_date") var registrationDate: Date,
    var password: String,
)