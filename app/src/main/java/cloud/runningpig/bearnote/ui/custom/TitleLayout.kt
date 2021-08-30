package cloud.runningpig.bearnote.ui.custom

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import cloud.runningpig.bearnote.R

class TitleLayout(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    init {
        LayoutInflater.from(context).inflate(R.layout.title_layout, this)
        val titleBack = findViewById<ImageView>(R.id.title_back)
        titleBack.setOnClickListener {
            val activity = context as Activity
            activity.finish()
        }
    }
}