package cloud.runningpig.bearnote.logic.dao

import androidx.room.*
import cloud.runningpig.bearnote.logic.model.*
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface NoteCategoryDao {

    /**
     * 类别管理
     */
    @Query("SELECT * FROM note_category WHERE id = :id")
    fun loadById(id: Int): Flow<NoteCategory>

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

    @Update
    suspend fun update(noteCategory: NoteCategory)

    // 查询某个收支类别下存在的记账笔数，如果存在返回1，否则返回null
    @Query("select 1 AS is_empty from note where note_category_id = :id LIMIT 1")
    suspend fun countCid(id: Int): Int?

    @Query("DELETE FROM note WHERE id = :id")
    suspend fun deleteNoteById(id: Int)

    /**
     * 账户管理
     */
    @Insert
    suspend fun insert(account: Account): Long

    @Transaction
    suspend fun insertAccount(account: Account) {
        val id = insert(account) // 1.添加账户，拿到自动生成的id
        val transfer = Transfer( // 2.添加转账记录：资金调整
            fromId = id.toInt(), // 现在临时转成Int，以后再考虑id是否用Int
            toId = id.toInt(),
            amount = account.balance, // 余额调整后的账户余额
            information = "+${account.balance}",
            date = Date()
        )
        insertTransfer(transfer)
    }

    @Query("SELECT MAX(`order`) FROM account")
    fun queryMaxOrder2(): Flow<Int>

    @Query("SELECT * FROM account ORDER BY `order` ASC")
    fun loadAccount(): Flow<List<Account>>

    @Update
    suspend fun updateList2(list: List<Account>)

    @Update
    suspend fun update(account: Account)

    @Insert
    suspend fun insertTransfer(transfer: Transfer)

    @Query("SELECT * FROM transfer_detail WHERE (accountId = :accountId OR fromId =:accountId OR toId = :accountId) AND noteDate BETWEEN :from AND :to ORDER BY noteDate DESC")
    fun queryByDate2(accountId: Int, from: Date, to: Date): Flow<List<TransferDetail>>

//    @Query("SELECT * FROM transfer_detail WHERE accountId = :aid OR fromId = :aid OR toId = :aid")
//    fun queryTransferByAid(aid: Int): Flow<List<TransferDetail>>

    @Query("SELECT * FROM account WHERE id = :aid")
    suspend fun queryByAid(aid: Int): Account

    @Query("SELECT * FROM account WHERE id = :aid")
    fun queryByAid2(aid: Int): Flow<Account>

    @Query("SELECT * FROM note_detail WHERE categoryId = :cid")
    suspend fun queryByCid(cid: Int): List<NoteDetail>

    @Transaction
    suspend fun deleteNoteCategory(cid: Int) {
        val noteDetailList = queryByCid(cid) // 查询类别下的所有记账
        noteDetailList.forEach { noteDetail ->
            if (noteDetail.accountId > -1) {
                val account = queryByAid(noteDetail.accountId) // 查询账户
                if (noteDetail.categorySort == 0) {
                    account.balance += noteDetail.noteAmount
                } else {
                    account.balance -= noteDetail.noteAmount
                }
                update(account) // 更新账户金额逆向回滚
            }
            deleteNoteById(noteDetail.noteId) // 账户资金回滚完毕，删除该记账
        }
    }

    @Query("SELECT SUM(balance) FROM account")
    fun sumBalance(): Flow<Double>

    @Query("DELETE FROM note WHERE account_id = :aid")
    suspend fun deleteNoteByAid(aid: Int)

    @Query("SELECT * FROM transfer WHERE from_id = :accountId OR to_id = :accountId")
    suspend fun queryByAid3(accountId: Int): List<Transfer>

    @Query("DELETE FROM transfer WHERE id = :id")
    suspend fun deleteTransferById(id: Int)

    @Query("DELETE FROM account WHERE id = :aid")
    suspend fun deleteAccountById(aid: Int)

    @Transaction
    suspend fun deleteAccount(aid: Int) {
        // 1. 删除该账户下所有的记账，不需要资金回滚，因为即将删除账户
        deleteNoteByAid(aid)
        // 2. 删除账户的转账记录，并回滚资金到其他账户
        val transferList = queryByAid3(aid)
        transferList.forEach { transfer ->
            if (transfer.fromId == aid) { // 转出，逆向回滚资金：toId账户-，本账户不必+，因为即将删除
                val account = queryByAid(transfer.toId)
                account.balance = account.balance - transfer.amount
                update(account)
            } else if (transfer.toId == aid) { // 转入，逆向回滚资金：fromId账户+，本账户不必-，因为即将删除
                val account = queryByAid(transfer.fromId)
                account.balance = account.balance + transfer.amount
                update(account)
            }
            // 其他：都相等代表账户金额调整，资金不用处理，直接删除该条
            deleteTransferById(transfer.id)
        }
        // 3. 删除账户
        deleteAccountById(aid)
    }

    @Query("SELECT * FROM note_detail WHERE noteId = :noteId")
    fun queryById(noteId: Int): NoteDetail

    @Transaction
    suspend fun deleteNote(noteId: Int) {
        val noteDetail = queryById(noteId) // 查询类别下的所有记账
        if (noteDetail.accountId > -1) {
            val account = queryByAid(noteDetail.accountId) // 查询账户
            if (noteDetail.categorySort == 0) {
                account.balance += noteDetail.noteAmount
            } else {
                account.balance -= noteDetail.noteAmount
            }
            update(account) // 更新账户金额逆向回滚
        }
        deleteNoteById(noteDetail.noteId) // 账户资金回滚完毕，删除该记账
    }
}