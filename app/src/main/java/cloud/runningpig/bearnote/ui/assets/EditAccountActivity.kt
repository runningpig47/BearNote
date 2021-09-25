package cloud.runningpig.bearnote.ui.assets

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cloud.runningpig.bearnote.BearNoteApplication
import cloud.runningpig.bearnote.BearNoteRepository
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.databinding.ActivityEditAccountBinding
import cloud.runningpig.bearnote.logic.dao.BearNoteDatabase
import cloud.runningpig.bearnote.logic.model.Account
import cloud.runningpig.bearnote.logic.model.IconMap
import cloud.runningpig.bearnote.ui.detail.DetailViewModel
import cloud.runningpig.bearnote.ui.detail.DetailViewModelFactory

class EditAccountActivity : AppCompatActivity() {

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
        val account = intent.getSerializableExtra("account") as Account
        _binding = ActivityEditAccountBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.apply {
            taTitleLayout.findViewById<TextView>(R.id.title_textView).text = "账户详情"
            taTitleLayout.findViewById<TextView>(R.id.title_textView2).text = "2021年10月1日"
            eaaImageView1.setImageResource(IconMap.map2[account.icon] ?: R.drawable.ic_error)
            eaaTextView1.text = account.name
            eaaTextView2.text = "账户余额 ${account.balance}"
            val adapter = EAAList1Adapter()
            adapter.setAccountId(account.id)
            eaaRecyclerView.adapter = adapter
            viewModel.queryByMonth2(account.id).observe(this@EditAccountActivity) {
                adapter.submitList(it)
            }
        }
    }

}