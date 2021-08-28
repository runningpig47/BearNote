package cloud.runningpig.bearnote

import cloud.runningpig.bearnote.logic.dao.NoteCategoryDao

class BearNoteRepository private constructor(private val noteCategoryDao: NoteCategoryDao) {

    fun loadBySort(sort: Int) = noteCategoryDao.loadBySort(sort)

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