package cloud.runningpig.bearnote.logic.dao

import androidx.room.Dao
import androidx.room.Insert
import cloud.runningpig.bearnote.logic.model.Note

@Dao
interface NoteDao {

    @Insert
    suspend fun insert(note: Note)

}