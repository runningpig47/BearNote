package cloud.runningpig.bearnote

import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import cloud.runningpig.bearnote.logic.dao.NoteCategoryDao
import cloud.runningpig.bearnote.logic.dao.NoteDao
import cloud.runningpig.bearnote.logic.model.Note
import cloud.runningpig.bearnote.logic.model.NoteCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.*

class BearNoteRepository private constructor(
    private val noteCategoryDao: NoteCategoryDao,
    private val noteDao: NoteDao
) {

    /**
     * 类别管理相关
     */
    fun loadBySort(sort: Int) = noteCategoryDao.loadBySort(sort)

    suspend fun updateList(list: List<NoteCategory>) = noteCategoryDao.updateList(list)

    suspend fun delete(list: List<NoteCategory>) = noteCategoryDao.delete(list)

    fun queryMaxOrder(sort: Int) = noteCategoryDao.queryMaxOrder(sort)

    suspend fun insert(noteCategory: NoteCategory) = noteCategoryDao.insert(noteCategory)

    /**
     * 记账相关
     */
    suspend fun insertNote(note: Note) = noteDao.insert(note)

    fun queryByDate(from: Date, to: Date) = noteDao.queryByDate(from, to)

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

//    fun queryDailyAmount(from: Date, to: Date) = noteDao.queryDailyAmount(from,to)

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

}