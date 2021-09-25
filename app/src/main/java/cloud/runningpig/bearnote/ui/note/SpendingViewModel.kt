package cloud.runningpig.bearnote.ui.note

import androidx.lifecycle.*
import cloud.runningpig.bearnote.BearNoteApplication
import cloud.runningpig.bearnote.BearNoteRepository
import cloud.runningpig.bearnote.logic.model.Account
import cloud.runningpig.bearnote.logic.model.Note
import cloud.runningpig.bearnote.logic.model.NoteCategory
import kotlinx.coroutines.launch
import java.util.*

class SpendingViewModel(private val bearNoteRepository: BearNoteRepository) : ViewModel() {

    var categoryItem: NoteCategory? = null // 记账选中的类别
    var accountItem: Account? = null // 记账时选中的账户
    val amount = MutableLiveData("0.0") // 记账金额
    var date: Date = Date()
    var note = MutableLiveData("")

    val page = MutableLiveData(0)

    fun loadBySort(sort: Int) = bearNoteRepository.loadBySort(sort).asLiveData()

    fun updateList(list: List<NoteCategory>) = viewModelScope.launch {
        bearNoteRepository.updateList(list)
    }

    fun delete(list: List<NoteCategory>) = viewModelScope.launch {
        bearNoteRepository.delete(list)
    }

    fun queryMaxOrder(sort: Int) = bearNoteRepository.queryMaxOrder(sort).asLiveData()

    private fun insert(noteCategory: NoteCategory) = viewModelScope.launch {
        bearNoteRepository.insert(noteCategory)
    }

    fun isEntryValid(icon: String): Boolean {
        if (icon.isBlank()) {
            return false
        }
        return true
    }

    private fun getNewItemEntry(name: String, icon: String, sort: Int, order: Int): NoteCategory {
        return NoteCategory(
            name = name,
            icon = icon,
            sort = sort,
            order = order,
            uid = BearNoteApplication.uid,
        )
    }

    fun addNewItem(name: String, icon: String, sort: Int, order: Int) {
        val newItem = getNewItemEntry(name, icon, sort, order)
        insert(newItem)
    }

    /**
     * 记账相关
     */
    private fun insertNote(note: Note) = viewModelScope.launch {
        bearNoteRepository.insertNote(note)
    }

    private fun getNewNoteEntry(noteCategoryId: Int, amount: Double, date: Date, information: String?, accountId: Int): Note {
        return Note(
            noteCategoryId = noteCategoryId,
            amount = amount,
            date = date,
            information = information,
            uid = BearNoteApplication.uid,
            accountId = accountId,
        )
    }

    // 验证记账的输入数据是否有效
    fun isNoteEntryValid(noteCategoryId: Int, amount: Double, date: Date, accountId: Int): Boolean {
        // TODO 以后修改验证细节
//        if (noteCategoryId == -1 || amount <= 0 || accountId == -1) {
//            return false
//        }
        return true
    }

    fun addNewNote(noteCategoryId: Int, amount: Double, date: Date, information: String?, accountId: Int) {
        val newNote = getNewNoteEntry(noteCategoryId, amount, date, information, accountId)
        insertNote(newNote)
    }

}

class SpendingViewModelFactory(private val bearNoteRepository: BearNoteRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SpendingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SpendingViewModel(bearNoteRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}