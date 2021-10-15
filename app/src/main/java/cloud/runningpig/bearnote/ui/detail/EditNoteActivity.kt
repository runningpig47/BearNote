package cloud.runningpig.bearnote.ui.detail

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import androidx.activity.viewModels
import cloud.runningpig.bearnote.BearNoteApplication
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.databinding.ActivityEditNoteBinding
import cloud.runningpig.bearnote.logic.BearNoteRepository
import cloud.runningpig.bearnote.logic.dao.BearNoteDatabase
import cloud.runningpig.bearnote.logic.model.IconMap
import cloud.runningpig.bearnote.ui.BaseActivity
import cloud.runningpig.bearnote.ui.note.NoteActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*

class EditNoteActivity : BaseActivity() {

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
        val noteId = intent.getIntExtra("noteId", -1) // TODO 处理-1
        viewModel.queryById(noteId).observe(this) { noteDetail ->
            if (noteDetail == null) {
                finish()
            } else {
                binding.apply {
                    aenImageView2.setImageResource(IconMap.map[noteDetail.categoryIcon] ?: R.drawable.ic_error)
                    aenTextView1.text = noteDetail.categoryName
                    aenTextView2.text = if (noteDetail.categorySort == 0) "支出" else "收入"
                    aenTextView3.text = noteDetail.noteAmount.toString()
                    val simpleDateFormat = SimpleDateFormat("yyyy年M月d日", Locale.getDefault())
                    aenTextView4.text = simpleDateFormat.format(noteDetail.noteDate)
                    var information = noteDetail.information
                    if (TextUtils.isEmpty(information)) {
                        information = noteDetail.categoryName
                    }
                    aenTextView5.text = information
                    val accountId = noteDetail.accountId
                    if (accountId == -1) {
                        aenTextView8.text = ""
                    } else {
                        aenTextView8.text = noteDetail.accountName
                    }
                    aenImageView1.setOnClickListener {
                        finish()
                    }
                    aenTextView6.setOnClickListener { // 编辑
                        val intent = Intent(this@EditNoteActivity, NoteActivity::class.java)
                        intent.putExtra("noteId", noteDetail.noteId)
                        intent.putExtra("categorySort", noteDetail.categorySort)
                        startActivity(intent)
                    }
                    aenTextView7.setOnClickListener {
                        showConfirmationDialog(noteDetail.noteId)
                    }
                }
            }
        }
    }

    private fun showConfirmationDialog(noteId: Int) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question3))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.deleteNote(noteId)
            }
            .show()
    }

}