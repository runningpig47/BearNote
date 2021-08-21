package cloud.runningpig.bearnote.logic.tools

import android.util.Log
import android.widget.Toast
import cloud.runningpig.bearnote.BearNoteApplication

object LogUtil {

    private const val VERBOSE = 1

    private const val DEBUG = 2

    private const val INFO = 3

    private const val WARN = 4

    private const val ERROR = 5

    private var level = 1

    fun v(tag: String, msg: String) {
        if (level <= VERBOSE) {
            Log.v(tag, msg)
        }
    }

    fun d(tag: String, msg: String) {
        if (level <= DEBUG) {
            Log.d(tag, msg)
        }
    }

    fun i(tag: String, msg: String) {
        if (level <= INFO) {
            Log.i(tag, msg)
        }
    }

    fun w(tag: String, msg: String) {
        if (level <= WARN) {
            Log.w(tag, msg)
        }
    }

    fun e(tag: String, msg: String) {
        if (level <= ERROR) {
            Log.e(tag, msg)
        }
    }

    /**
     * 截断输出长日志
     */
    fun dl(tag: String, msg: String) {
        if (tag.isNotEmpty() && msg.isNotEmpty()) {
            var msg1 = msg
            val segmentSize = 3 * 1024
            val length = msg1.length.toLong()
            if (length <= segmentSize) { // 长度小于等于限制直接打印
                d(tag, msg1)
            } else {
                while (msg1.length > segmentSize) { // 循环分段打印日志
                    val logContent = msg1.substring(0, segmentSize)
                    msg1 = msg1.replace(logContent, "")
                    d(tag, logContent)
                }
                d(tag, msg1) // 打印剩余日志
            }
        }
    }

}

fun String.showToast(duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(BearNoteApplication.context, this, duration).show()
}

fun Int.showToast(duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(BearNoteApplication.context, this, duration).show()
}