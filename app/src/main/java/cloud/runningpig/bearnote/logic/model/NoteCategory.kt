package cloud.runningpig.bearnote.logic.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_category")
data class NoteCategory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val icon: String,
    val sort: Int,
    val order: Int,
    val uid: Int,
    val recorded: Boolean,
    @ColumnInfo(name = "is_upload") val isUpload: Int
)
