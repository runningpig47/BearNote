package cloud.runningpig.bearnote.logic.model

import androidx.room.DatabaseView
import java.io.Serializable
import java.util.*

@DatabaseView(
    viewName = "note_detail", value =
    "SELECT note.id AS noteId, note.amount as noteAmount, note.date as noteDate, note.information, note.uid, " +
            "note.note_category_id AS categoryId, note_category.sort AS categorySort, " +
            "note_category.icon AS categoryIcon, note_category.name AS categoryName " +
            "FROM note INNER JOIN note_category ON note.note_category_id = note_category.id"
)
class NoteDetail(
    val noteId: Int,
    var noteAmount: Double,
    var noteDate: Date,
    var information: String?,
    val uid: Int,
    val categoryId: Int,
    var categorySort: Int,
    var categoryIcon: String,
    var categoryName: String,
) : Serializable