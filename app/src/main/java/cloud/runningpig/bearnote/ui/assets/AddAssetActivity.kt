package cloud.runningpig.bearnote.ui.assets

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import cloud.runningpig.bearnote.BearNoteApplication
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.databinding.ActivityAddAssetBinding
import cloud.runningpig.bearnote.logic.BearNoteRepository
import cloud.runningpig.bearnote.logic.dao.BearNoteDatabase
import cloud.runningpig.bearnote.logic.model.Account
import cloud.runningpig.bearnote.logic.model.IconMap
import cloud.runningpig.bearnote.logic.utils.ViewUtil
import cloud.runningpig.bearnote.logic.utils.showToast
import cloud.runningpig.bearnote.ui.BaseActivity
import cloud.runningpig.bearnote.ui.detail.DetailViewModel
import cloud.runningpig.bearnote.ui.detail.DetailViewModelFactory
import java.util.*

class AddAssetActivity : BaseActivity() {

    private var _binding: ActivityAddAssetBinding? = null
    private val binding get() = _binding!!
    private lateinit var iconName: String
    private var order: Int = Int.MAX_VALUE
    lateinit var item: Account

    val viewModel: DetailViewModel by viewModels {
        val repository = BearNoteRepository.getInstance(
            BearNoteDatabase.getDatabase(BearNoteApplication.context).noteCategoryDao(),
            BearNoteDatabase.getDatabase(BearNoteApplication.context).noteDao()
        )
        DetailViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddAssetBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        val accountId = intent.getIntExtra("accountId", -1)
        if (accountId == -1) { // 添加账户
            iconName = intent.getStringExtra("iconName") ?: ""
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
        } else { // 修改账户
            viewModel.queryByAid2(accountId).observe(this) { account ->
                item = account
                iconName = account.icon
                binding.aaaTitleLayout.findViewById<TextView>(R.id.title_textView).text = "修改账户"
                val drawable: Drawable? = AppCompatResources.getDrawable(this, IconMap.map2[iconName] ?: R.drawable.ic_error)
                drawable?.setBounds(0, 0, ViewUtil.dp2px(this, 24f), ViewUtil.dp2px(this, 24f))
                binding.itemName.setCompoundDrawables(null, null, drawable, null)
                binding.itemName.setText(account.name)
                binding.itemPrice.setText(account.balance.toString())
                binding.itemInfo.setText(account.information)
                binding.saveAction.visibility = View.VISIBLE
                binding.saveAction.setOnClickListener {
                    updateItem()
                }
            }
        }
    }

    private fun accountEntryValid(): Boolean {
        return viewModel.accountEntryValid(
            binding.itemName.text.toString(),
            binding.itemPrice.text.toString()
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

    private fun updateItem() {
        if (accountEntryValid()) {
            // 更新账户前，先检查账户余额是否发生变动。如果发生变动，添加转账记录。
            val oldBalance = item.balance
            val newBalance = binding.itemPrice.text.toString().toDouble()
            val minus = newBalance - oldBalance
            val info = if (minus > 0) "+$minus" else "$minus"
            if (oldBalance != newBalance) { // 1.金额被修改，先添加转账记录
                viewModel.addNewTransfer(
                    item.id,
                    item.id,
                    newBalance,
                    info,
                    Date()
                )
            }
            viewModel.updateAccount( // 2.更新账户
                item.id,
                binding.itemName.text.toString(),
                iconName,
                binding.itemPrice.text.toString().toDouble(),
                binding.itemInfo.text.toString(),
                item.order
            )
            finish()
        } else {
            "填写错误".showToast()
        }
    }

}