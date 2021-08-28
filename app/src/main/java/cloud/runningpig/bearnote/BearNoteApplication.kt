package cloud.runningpig.bearnote

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import cloud.runningpig.bearnote.logic.dao.BearNoteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class BearNoteApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        const val TAG = "BearNoteApplicationLog"
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

}