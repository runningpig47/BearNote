package cloud.runningpig.bearnote.ui.assets

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.logic.utils.ViewUtil

class SADialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        return dialog.apply {
//            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCanceledOnTouchOutside(true)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.select_account_dialog, container, false)
        listener?.bindView(view)
        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.apply {
            window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.WHITE))
                val layoutParams = attributes
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
                layoutParams.height = ViewUtil.dp2px(context, 320f)
                layoutParams.gravity = Gravity.BOTTOM
                layoutParams.dimAmount = 0.2f
                attributes = layoutParams
            }
        }
    }

    private var listener: OnBindViewListener? = null

    fun setOnBindViewListener(listener: OnBindViewListener) {
        this.listener = listener
    }

    interface OnBindViewListener {
        fun bindView(view: View)
    }

}