package cloud.runningpig.bearnote.logic.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_category")
data class NoteCategory(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var name: String,
    var icon: String,
    var sort: Int,
    var order: Int,
    var uid: Int,
    var recorded: Boolean,
    @ColumnInfo(name = "is_upload") var isUpload: Int
)
