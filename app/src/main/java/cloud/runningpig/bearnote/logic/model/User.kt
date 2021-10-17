package cloud.runningpig.bearnote.logic.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 用户表
 * @param id 用户id
 * @param userName 昵称
 */
@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) var id: Int = -1,
    var username: String,
    val password: String,
    val nickname: String
)