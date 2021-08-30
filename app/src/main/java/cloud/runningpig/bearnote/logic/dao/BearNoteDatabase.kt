package cloud.runningpig.bearnote.logic.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import cloud.runningpig.bearnote.logic.model.NoteCategory

@Database(entities = [NoteCategory::class], version = 1)
abstract class BearNoteDatabase : RoomDatabase() {

    abstract fun noteCategoryDao(): NoteCategoryDao

    private class BearNoteDatabaseCallback :
        RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let {
            }
        }
    }

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
                    .addCallback(BearNoteDatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}