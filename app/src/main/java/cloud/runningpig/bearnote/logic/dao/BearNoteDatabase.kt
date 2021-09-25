package cloud.runningpig.bearnote.logic.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import cloud.runningpig.bearnote.logic.model.*

@Database(
    entities = [NoteCategory::class, Note::class, Account::class, Transfer::class],
    views = [NoteDetail::class, TransferDetail::class], version = 1
)
@TypeConverters(Converters::class)
abstract class BearNoteDatabase : RoomDatabase() {

    abstract fun noteCategoryDao(): NoteCategoryDao
    abstract fun noteDao(): NoteDao

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