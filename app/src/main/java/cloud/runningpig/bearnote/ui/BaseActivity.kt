package cloud.runningpig.bearnote.ui

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import cloud.runningpig.bearnote.logic.utils.LogUtil

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtil.d("BaseActivity", javaClass.simpleName)
    }

}