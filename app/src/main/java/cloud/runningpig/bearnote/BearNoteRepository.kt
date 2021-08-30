package cloud.runningpig.bearnote

import cloud.runningpig.bearnote.logic.dao.NoteCategoryDao
import cloud.runningpig.bearnote.logic.model.NoteCategory

class BearNoteRepository private constructor(private val noteCategoryDao: NoteCategoryDao) {

    fun loadBySort(sort: Int) = noteCategoryDao.loadBySort(sort)

    suspend fun updateList(list: List<NoteCategory>) = noteCategoryDao.updateList(list)

    suspend fun delete(list: List<NoteCategory>) = noteCategoryDao.delete(list)

    fun queryMaxOrder(sort: Int) = noteCategoryDao.queryMaxOrder(sort)

    suspend fun insert(noteCategory: NoteCategory) = noteCategoryDao.insert(noteCategory)

    companion object {
        @Volatile
        private var INSTANCE: BearNoteRepository? = null

        fun getInstance(noteCategoryDao: NoteCategoryDao) =
            INSTANCE ?: synchronized(this) {
                val instance = BearNoteRepository(noteCategoryDao)
                INSTANCE = instance
                instance
            }
    }

}