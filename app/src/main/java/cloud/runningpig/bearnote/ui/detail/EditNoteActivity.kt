package cloud.runningpig.bearnote.ui.detail

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cloud.runningpig.bearnote.BearNoteApplication
import cloud.runningpig.bearnote.BearNoteRepository
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.databinding.ActivityEditNoteBinding
import cloud.runningpig.bearnote.logic.dao.BearNoteDatabase
import cloud.runningpig.bearnote.logic.model.IconMap
import cloud.runningpig.bearnote.logic.model.NoteDetail
import java.text.SimpleDateFormat
import java.util.*

class EditNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditNoteBinding

    val viewModel: DetailViewModel by viewModels {
        val repository = BearNoteRepository.getInstance(
            BearNoteDatabase.getDatabase(BearNoteApplication.context).noteCategoryDao(),
            BearNoteDatabase.getDatabase(BearNoteApplication.context).noteDao()
        )
        DetailViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        val intent = intent
        val noteDetail = intent.getSerializableExtra("noteDetail") as NoteDetail
        binding.aenImageView2.setImageResource(IconMap.map[noteDetail.categoryIcon] ?: R.drawable.ic_error)
        binding.aenTextView1.text = noteDetail.categoryName
        binding.aenTextView2.text = if (noteDetail.categorySort == 0) "支出" else "收入"
        binding.aenTextView3.text = noteDetail.noteAmount.toString()
        val simpleDateFormat = SimpleDateFormat("yyyy年M月d日", Locale.getDefault())
        binding.aenTextView4.text = simpleDateFormat.format(noteDetail.noteDate)
        var information = noteDetail.information
        if (TextUtils.isEmpty(information)) {
            information = noteDetail.categoryName
        }
        binding.aenTextView5.text = information
        binding.aenImageView1.setOnClickListener {
            finish()
        }
        binding.aenTextView6.setOnClickListener {
            // TODO 编辑
        }
        binding.aenTextView7.setOnClickListener {
            // TODO 删除
        }
    }
}