package cloud.runningpig.bearnote.logic.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import cloud.runningpig.bearnote.logic.model.NoteCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [NoteCategory::class], version = 1)
abstract class BearNoteDatabase : RoomDatabase() {

    abstract fun noteCategoryDao(): NoteCategoryDao

//    private class BearNoteDatabaseCallback(private val scope: CoroutineScope) :
//        RoomDatabase.Callback() {
//        override fun onCreate(db: SupportSQLiteDatabase) {
//            super.onCreate(db)
//            INSTANCE?.let {
//                scope.launch {
//                    val noteCategoryDao = it.noteCategoryDao()
//                    // Delete all content here.
//                    noteCategoryDao.deleteAll()
//                    // Add sample item.
//                    var noteCategory1 = NoteCategory(name = "BTC",icon = "BTC",sort = 0,order = 0,uid = 0,recorded = false,isUpload = 0)
//                    var noteCategory2 = NoteCategory(name = "BTC",icon = "BTC",sort = 0,order = 0,uid = 0,recorded = false,isUpload = 0)
//                    var noteCategory3 = NoteCategory(name = "BTC",icon = "BTC",sort = 0,order = 0,uid = 0,recorded = false,isUpload = 0)
//                    val list = ArrayList<NoteCategory>()
//                    list.add(noteCategory1)
//                    list.add(noteCategory2)
//                    list.add(noteCategory3)
//                    noteCategoryDao.insertDefault(list)
//                }
//            }
//        }
//    }

    companion object {
        @Volatile
        private var INSTANCE: BearNoteDatabase? = null

        fun getDatabase(context: Context): BearNoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BearNoteDatabase::class.java,
                    "bearnote_database"
                )
//                    .addCallback(BearNoteDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}