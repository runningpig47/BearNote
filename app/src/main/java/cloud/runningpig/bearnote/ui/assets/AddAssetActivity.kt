package cloud.runningpig.bearnote.ui.assets

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import cloud.runningpig.bearnote.BearNoteApplication
import cloud.runningpig.bearnote.BearNoteRepository
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.databinding.ActivityAddAssetBinding
import cloud.runningpig.bearnote.logic.dao.BearNoteDatabase
import cloud.runningpig.bearnote.logic.model.IconMap
import cloud.runningpig.bearnote.logic.utils.ViewUtil
import cloud.runningpig.bearnote.logic.utils.showToast
import cloud.runningpig.bearnote.ui.detail.DetailViewModel
import cloud.runningpig.bearnote.ui.detail.DetailViewModelFactory

class AddAssetActivity : AppCompatActivity() {

    private var _binding: ActivityAddAssetBinding? = null
    private val binding get() = _binding!!
    private lateinit var iconName: String
    private var order: Int = Int.MAX_VALUE

    val viewModel: DetailViewModel by viewModels {
        val repository = BearNoteRepository.getInstance(
            BearNoteDatabase.getDatabase(BearNoteApplication.context).noteCategoryDao(),
            BearNoteDatabase.getDatabase(BearNoteApplication.context).noteDao()
        )
        DetailViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        iconName = intent.getStringExtra("iconName") ?: ""
        _binding = ActivityAddAssetBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.aaaTitleLayout.findViewById<TextView>(R.id.title_textView).text = "添加账户"
        val drawable: Drawable? = AppCompatResources.getDrawable(this, IconMap.map2[iconName] ?: R.drawable.ic_error)
        drawable?.setBounds(0, 0, ViewUtil.dp2px(this, 24f), ViewUtil.dp2px(this, 24f))
        binding.itemName.setCompoundDrawables(null, null, drawable, null)
        viewModel.queryMaxOrder2().observe(this) {
            binding.saveAction.visibility = View.VISIBLE
            binding.saveAction.setOnClickListener {
                addNewItem()
            }
        }
    }

    private fun accountEntryValid(): Boolean {
        return viewModel.accountEntryValid(
            binding.itemName.text.toString(),
            iconName,
            binding.itemPrice.text.toString().toDouble(), // TODO 验证输入
            binding.itemInfo.text.toString(),
            order
        )
    }

    private fun addNewItem() {
        if (accountEntryValid()) {
            viewModel.addNewAccount(
                binding.itemName.text.toString(),
                iconName,
                binding.itemPrice.text.toString().toDouble(),
                binding.itemInfo.text.toString(),
                order
            )
            setResult(RESULT_OK)
            finish()
        } else {
            "填写错误".showToast()
        }
    }

}