package cloud.runningpig.bearnote.logic.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cloud.runningpig.bearnote.BearNoteApplication
import java.util.*

@Entity(tableName = "transfer")
data class Transfer(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "from_id") var fromId: Int,
    @ColumnInfo(name = "to_id") var toId: Int,
    var amount: Double,
    var information: String?,
    var date: Date,
    var uid: Int = BearNoteApplication.uid
)
