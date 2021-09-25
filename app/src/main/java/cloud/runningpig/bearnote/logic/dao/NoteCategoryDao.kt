package cloud.runningpig.bearnote.logic.dao

import androidx.room.*
import cloud.runningpig.bearnote.logic.model.Account
import cloud.runningpig.bearnote.logic.model.NoteCategory
import cloud.runningpig.bearnote.logic.model.Transfer
import cloud.runningpig.bearnote.logic.model.TransferDetail
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface NoteCategoryDao {

    /**
     * 类别管理
     */
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

    /**
     * 账户管理
     */
    @Insert
    suspend fun insert(account: Account)

    @Query("SELECT MAX(`order`) FROM account")
    fun queryMaxOrder2(): Flow<Int>

    @Query("SELECT * FROM account ORDER BY `order` ASC")
    fun loadAccount(): Flow<List<Account>>

    @Update
    suspend fun updateList2(list: List<Account>)

    @Insert
    suspend fun insertTransfer(transfer: Transfer)

    @Query("SELECT * FROM transfer_detail WHERE (accountId = :accountId OR fromId =:accountId OR toId = :accountId) AND noteDate BETWEEN :from AND :to ORDER BY noteDate DESC")
    fun queryByDate2(accountId: Int, from: Date, to: Date): Flow<List<TransferDetail>>

}