package cloud.runningpig.bearnote.logic.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "memo")
data class Memo(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var title: String,
    var content: String,
    var date: Date,
    var uid: Int,
)