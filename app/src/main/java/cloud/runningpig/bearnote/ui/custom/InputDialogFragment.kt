package cloud.runningpig.bearnote.ui.custom

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.logic.utils.Injector
import cloud.runningpig.bearnote.logic.utils.ViewUtil
import cloud.runningpig.bearnote.ui.note.SpendingViewModel

class InputDialogFragment : DialogFragment() {

    private val viewModel: SpendingViewModel by activityViewModels {
        Injector.providerSpendingViewModelFactory(requireContext())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        return dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCanceledOnTouchOutside(true)
            setOnKeyListener { _, keyCode, _ ->
                when (keyCode) {
                    KeyEvent.KEYCODE_ENTER,
                    KeyEvent.KEYCODE_BACK,
                    -> {
                        dismiss()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.input_edit_layout, container, false)
        val editText = view.findViewById<EditText>(R.id.input_editText)
        editText.setText(viewModel.note.value)
        editText.addTextChangedListener {
            viewModel.note.value = it.toString()
        }
        listener?.bindView(view)
        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.apply {
            window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val layoutParams = attributes
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
                layoutParams.height = ViewUtil.dp2px(context, 45F)
                layoutParams.gravity = Gravity.BOTTOM
                layoutParams.dimAmount = 0.2F
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