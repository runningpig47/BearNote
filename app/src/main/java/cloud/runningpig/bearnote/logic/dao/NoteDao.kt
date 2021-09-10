package cloud.runningpig.bearnote.logic.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cloud.runningpig.bearnote.BearNoteApplication
import cloud.runningpig.bearnote.logic.model.Note
import cloud.runningpig.bearnote.logic.model.NoteDetail
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface NoteDao {

    @Insert
    suspend fun insert(note: Note)

    @Query("SELECT * FROM note_detail WHERE uid = :uid AND noteDate BETWEEN :from AND :to")
    fun queryByDate(from: Date, to: Date, uid: Int = BearNoteApplication.uid): Flow<List<NoteDetail>>
}