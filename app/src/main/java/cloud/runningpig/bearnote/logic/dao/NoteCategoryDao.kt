package cloud.runningpig.bearnote.logic.dao

import androidx.room.*
import cloud.runningpig.bearnote.logic.model.NoteCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteCategoryDao {

    @Query("SELECT * FROM note_category WHERE sort = :sort ORDER BY `order` ASC")
    fun loadBySort(sort: Int): Flow<List<NoteCategory>>

    @Update
    suspend fun updateList(list: List<NoteCategory>)

    @Delete
    suspend fun delete(list: List<NoteCategory>)

    @Query("SELECT MAX(`order`) FROM note_category WHERE sort = :sort")
    fun queryMaxOrder(sort: Int): Flow<Int>

    @Insert
    suspend fun insert(noteCategory: NoteCategory)

//    @Query("SELECT * FROM user")
//    fun getAll(): List<User>
//
//    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
//    fun loadAllByIds(userIds: IntArray): List<User>
//
//    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): User
//
//    @Insert
//    fun insertAll(vararg users: User)
//
//    @Delete
//    fun delete(user: User)

}