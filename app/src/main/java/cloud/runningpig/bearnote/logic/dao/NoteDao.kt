package cloud.runningpig.bearnote.logic.dao

import androidx.room.*
import cloud.runningpig.bearnote.BearNoteApplication
import cloud.runningpig.bearnote.logic.model.ChartMonthBean
import cloud.runningpig.bearnote.logic.model.DailyAmount
import cloud.runningpig.bearnote.logic.model.Note
import cloud.runningpig.bearnote.logic.model.NoteDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.*

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: Note)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(note: Note)

    @Query("SELECT * FROM note_detail WHERE uid = :uid AND noteDate BETWEEN :from AND :to ORDER BY noteDate DESC")
    fun queryByDate(from: Date, to: Date, uid: Int = BearNoteApplication.uid): Flow<List<NoteDetail>>

    @Query("SELECT categoryName AS category_name, categoryIcon AS category_icon, COUNT(categoryId) AS count_category_id, SUM(noteAmount) AS sum_note_amount FROM note_detail WHERE uid = :uid AND categorySort = :sort AND noteDate BETWEEN :from AND :to GROUP BY categoryId ORDER BY SUM(noteAmount) DESC")
    fun queryByMonth(sort: Int, from: Date, to: Date, uid: Int = BearNoteApplication.uid): Flow<List<ChartMonthBean>>

    @Query("SELECT categorySort AS sort, SUM(noteAmount) AS amount FROM note_detail WHERE uid = :uid AND noteDate BETWEEN :from AND :to GROUP BY categorySort ORDER BY categorySort DESC")
    fun queryDailyAmountA(from: Date, to: Date, uid: Int = BearNoteApplication.uid): Flow<List<DailyAmount>>
    fun queryDailyAmount(from: Date, to: Date) = queryDailyAmountA(from, to).distinctUntilChanged()

    @Query("SELECT * FROM note_detail WHERE noteId = :noteId")
    fun queryById(noteId: Int): Flow<NoteDetail>

}