package cloud.runningpig.bearnote.logic

import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import cloud.runningpig.bearnote.logic.dao.NoteCategoryDao
import cloud.runningpig.bearnote.logic.dao.NoteDao
import cloud.runningpig.bearnote.logic.model.*
import cloud.runningpig.bearnote.logic.network.BearNoteNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.*
import kotlin.coroutines.CoroutineContext

class BearNoteRepository private constructor(
    private val noteCategoryDao: NoteCategoryDao, private val noteDao: NoteDao
) {

    fun loadById(id: Int) = noteCategoryDao.loadById(id)

    fun loadBySort(sort: Int) = noteCategoryDao.loadBySort(sort)

    suspend fun updateList(list: List<NoteCategory>) = noteCategoryDao.updateList(list)

    suspend fun delete(list: List<NoteCategory>) = noteCategoryDao.delete(list)

    fun queryMaxOrder(sort: Int) = noteCategoryDao.queryMaxOrder(sort)

    suspend fun insert(noteCategory: NoteCategory) = noteCategoryDao.insert(noteCategory)

    suspend fun update(noteCategory: NoteCategory) = noteCategoryDao.update(noteCategory)

    suspend fun countCid(id: Int) = noteCategoryDao.countCid(id)

    suspend fun deleteNoteCategory(cid: Int) = noteCategoryDao.deleteNoteCategory(cid)

    /** 记账相关 */
    suspend fun insertNote(note: Note) = noteDao.insert(note)

    suspend fun updateNote(note: Note) = noteDao.update(note)

    fun queryByDate(from: Date, to: Date) = noteDao.queryByDate(from, to)

    fun queryById(noteId: Int) = noteDao.queryById(noteId)

    /** 图表按月查询View */
    fun queryByMonth(sort: Int, from: Date, to: Date) = noteDao.queryByMonth(sort, from, to)

    fun queryDailyAmount(from: Date, to: Date) = liveData(Dispatchers.IO) {
        val t = coroutineScope {
            val deferredAmount = async {
                noteDao.queryDailyAmount(from, to).asLiveData()
            }
            // TODO 添加另一个日历查询请求
            deferredAmount.await()
        }
        emitSource(t)
    }

    /** 账户相关 */

    suspend fun insertAccount(account: Account) = noteCategoryDao.insertAccount(account)

    suspend fun update(account: Account) = noteCategoryDao.update(account)

    fun queryMaxOrder2() = noteCategoryDao.queryMaxOrder2()

    fun loadAccount() = noteCategoryDao.loadAccount()

    suspend fun updateList2(list: List<Account>) = noteCategoryDao.updateList2(list)

    suspend fun insertTransfer(transfer: Transfer) = noteCategoryDao.insertTransfer(transfer)

    fun queryByDate2(accountId: Int, from: Date, to: Date) = noteCategoryDao.queryByDate2(accountId, from, to)

    fun sumBalance() = noteCategoryDao.sumBalance()

    fun queryByAid2(aid: Int) = noteCategoryDao.queryByAid2(aid)

    suspend fun queryByAid3(aid: Int) = noteCategoryDao.queryByAid(aid)

    suspend fun deleteAccount(aid: Int) = noteCategoryDao.deleteAccount(aid)

    suspend fun deleteNote(noteId: Int) = noteCategoryDao.deleteNote(noteId)

    companion object {
        @Volatile
        private var INSTANCE: BearNoteRepository? = null

        fun getInstance(noteCategoryDao: NoteCategoryDao, noteDao: NoteDao) =
            INSTANCE ?: synchronized(this) {
                val instance = BearNoteRepository(noteCategoryDao, noteDao)
                INSTANCE = instance
                instance
            }
    }

    private fun <T> fire(
        context: CoroutineContext = Dispatchers.IO,
        block: suspend () -> Result<T>
    ) =
        liveData(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure(e)
            }
            emit(result)
        }

    suspend fun addUser(user: User) = BearNoteNetwork.addUser(user)

    suspend fun login(username: String, password: String) = BearNoteNetwork.login(username, password)

    suspend fun insertUser(user: User) = noteCategoryDao.insertUser(user)

    fun selectUser() = noteCategoryDao.selectUser()

    suspend fun deleteUser() = noteCategoryDao.deleteUser()

}