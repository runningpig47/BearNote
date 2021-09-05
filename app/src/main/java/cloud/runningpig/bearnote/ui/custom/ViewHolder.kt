package cloud.runningpig.bearnote.ui.custom

import android.text.TextUtils
import android.util.SparseArray
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val mViews: SparseArray<View?> = SparseArray()
    private val mRootView: View = itemView

    fun <T : View> findViewById(id: Int): T {
        var view = mViews.get(id)
        if (view == null) {
            view = itemView.findViewById(id)
            mViews.put(id, view)
        }
        return view as T
    }

    fun getRootView(): View = mRootView

    fun setText(id: Int, text: String): ViewHolder {
        if (!TextUtils.isEmpty(text) && id != 0) {
            val view = findViewById<View>(id)
            if (view is TextView) {
                view.text = text
            }
        }
        return this
    }

}