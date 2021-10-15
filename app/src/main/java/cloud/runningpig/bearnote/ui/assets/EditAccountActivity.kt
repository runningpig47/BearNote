package cloud.runningpig.bearnote.ui.assets

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.viewModels
import cloud.runningpig.bearnote.BearNoteApplication
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.databinding.ActivityEditAccountBinding
import cloud.runningpig.bearnote.logic.BearNoteRepository
import cloud.runningpig.bearnote.logic.dao.BearNoteDatabase
import cloud.runningpig.bearnote.logic.model.IconMap
import cloud.runningpig.bearnote.ui.BaseActivity
import cloud.runningpig.bearnote.ui.detail.DetailViewModel
import cloud.runningpig.bearnote.ui.detail.DetailViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EditAccountActivity : BaseActivity() {

    private var _binding: ActivityEditAccountBinding? = null
    private val binding get() = _binding!!

    val viewModel: DetailViewModel by viewModels {
        val repository = BearNoteRepository.getInstance(
            BearNoteDatabase.getDatabase(BearNoteApplication.context).noteCategoryDao(),
            BearNoteDatabase.getDatabase(BearNoteApplication.context).noteDao()
        )
        DetailViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEditAccountBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.taTitleLayout.findViewById<TextView>(R.id.title_textView).text = "账户详情"
        binding.taTitleLayout.findViewById<TextView>(R.id.title_textView2).text = "2021年10月1日"
        val accountId = intent.getIntExtra("accountId", -1) // TODO 处理-1
        viewModel.queryByAid2(accountId).observe(this) { account ->
            account?.let {
                binding.eaaImageView1.setImageResource(IconMap.map2[account.icon] ?: R.drawable.ic_error)
                binding.eaaTextView1.text = account.name
                binding.eaaTextView2.text = "账户余额 ${account.balance}"
            }
        }
        val adapter = EAAList1Adapter()
        adapter.setAccountId(accountId)
        binding.eaaRecyclerView.adapter = adapter
        viewModel.queryByMonth2(accountId).observe(this) {
            adapter.submitList(it)
            // 计算累计收入、支出
            var incomeByMonth = .0
            var spendingByMonth = .0
            it.forEach { transferDetail ->
                val accountSort = transferDetail.detailSort
                if (accountSort == 0) { // 收支明细
                    val sort = transferDetail.categorySort
                    if (sort == 0) { // 支出
                        spendingByMonth -= transferDetail.noteAmount
                    } else {
                        incomeByMonth += transferDetail.noteAmount
                    }
                } else { // 转账记录
                    when {
                        transferDetail.fromId == transferDetail.toId -> { // 资金调整
                            try {
                                val info = transferDetail.information
                                val amount = info.toDouble()
                                if (amount > 0) {
                                    incomeByMonth += amount
                                } else {
                                    spendingByMonth += amount
                                }
                            } catch (e: Exception) { // toDouble异常
                            }
                        }
                        transferDetail.fromId == accountId -> { // 转出
                            spendingByMonth -= transferDetail.noteAmount
                        }
                        transferDetail.toId == accountId -> { // 转入
                            incomeByMonth += transferDetail.noteAmount
                        }
                    }
                }
            }
            binding.eaaTextView3.text = incomeByMonth.toString()
            binding.eaaTextView4.text = spendingByMonth.toString()
        }
        binding.eaaTextView5.setOnClickListener {
            val intent = Intent(this, AddAssetActivity::class.java)
            intent.putExtra("accountId", accountId)
            startActivity(intent)
        }
        binding.eaaTextView6.setOnClickListener {
            showConfirmationDialog(accountId)
        }
    }

    private fun showConfirmationDialog(accountId: Int) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question2))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.deleteAccount(accountId)
                finish()
            }
            .show()
    }

}