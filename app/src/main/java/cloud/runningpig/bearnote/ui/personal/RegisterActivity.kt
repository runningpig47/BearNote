package cloud.runningpig.bearnote.ui.personal

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cloud.runningpig.bearnote.BearNoteApplication
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.databinding.ActivityRegisterBinding
import cloud.runningpig.bearnote.logic.BearNoteRepository
import cloud.runningpig.bearnote.logic.dao.BearNoteDatabase
import cloud.runningpig.bearnote.logic.model.User
import cloud.runningpig.bearnote.logic.utils.showToast
import cloud.runningpig.bearnote.ui.detail.DetailViewModel
import cloud.runningpig.bearnote.ui.detail.DetailViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private var _binding: ActivityRegisterBinding? = null
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
        _binding = ActivityRegisterBinding.inflate(LayoutInflater.from(this))
        binding.aaaTitleLayout.findViewById<TextView>(R.id.title_textView).text = "注册"
        setContentView(binding.root)
        binding.register.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()
            val nickname = binding.nickname.text.toString()
            if (viewModel.userEntryValid(username, password, nickname)) {
                val user = User(username = username, password = password, nickname = nickname)
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val result = viewModel.addUser(user) // TODO 阅读博客，解决不规范JSON返回异常
                        withContext(Dispatchers.Main) {
                            result.showToast(Toast.LENGTH_LONG)
                            if (result == "success") {
                                finish()
                            }
                        }
                    } catch (e: Exception) {
                    }
                }
            } else {
                "填写错误".showToast()
            }
        }
    }

}