package cloud.runningpig.bearnote.ui.custom

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView


class NTextView : AppCompatTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val typeFace = Typeface.createFromAsset(context.assets, "fonts/EU-XFZ.TTF")
        typeface = typeFace
    }

    constructor(context: Context, attrs: AttributeSet?, style: Int) : super(context, attrs, style)

}