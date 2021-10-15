package cloud.runningpig.bearnote.ui.note

import android.text.TextUtils
import androidx.lifecycle.*
import cloud.runningpig.bearnote.BearNoteApplication
import cloud.runningpig.bearnote.logic.BearNoteRepository
import cloud.runningpig.bearnote.logic.model.Account
import cloud.runningpig.bearnote.logic.model.Note
import cloud.runningpig.bearnote.logic.model.NoteCategory
import kotlinx.coroutines.launch
import java.util.*

class SpendingViewModel(private val bearNoteRepository: BearNoteRepository) : ViewModel() {

    var noteId: Int = -1 // 用于标记是否修改记账
    var categorySort:Int = 0

    var categoryId: Int = -1 // 记账选中的类别id
    var accountItem = MutableLiveData<Account?>()// 记账时选中的账户，已观察
    var date: Date = Date() // 不监听是为了保留今日图标样式
    val amount = MutableLiveData("0.0") // 记账金额，已观察
    var info = MutableLiveData("") // 记账备注，已观察
    val page = MutableLiveData(0)

    var oldAccountItem: Account? = null // 更新账户前，该记账旧的账户
    var oldAmount: String = "0.0"

    fun loadById(id: Int) = bearNoteRepository.loadById(id).asLiveData()

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

    private fun update(noteCategory: NoteCategory) = viewModelScope.launch {
        bearNoteRepository.update(noteCategory)
    }

    suspend fun countCid(id: Int) = bearNoteRepository.countCid(id)

    suspend fun deleteNoteCategory(cid: Int) = bearNoteRepository.deleteNoteCategory(cid)

    fun isEntryValid(name: String): Boolean {
        if (name.isBlank()) {
            return false
        }
        return true
    }

    private fun getNewItemEntry(name: String, icon: String, sort: Int, order: Int, id: Int = -1): NoteCategory {
        if (id == -1) { // 不手动指定id，新增元素
            return NoteCategory(
                name = name,
                icon = icon,
                sort = sort,
                order = order,
                uid = BearNoteApplication.uid,
            )
        } else { // 指定id，说明是更新某个id的元素
            return NoteCategory(
                id = id,
                name = name,
                icon = icon,
                sort = sort,
                order = order,
                uid = BearNoteApplication.uid,
            )
        }
    }

    fun addNewItem(name: String, icon: String, sort: Int, order: Int) {
        val newItem = getNewItemEntry(name, icon, sort, order)
        insert(newItem)
    }

    fun updateItem(id: Int, name: String, icon: String, sort: Int, order: Int) {
        val newItem = getNewItemEntry(name, icon, sort, order, id)
        update(newItem)
    }

    /**
     * 记账相关
     */
    private fun insertNote(note: Note) = viewModelScope.launch {
        bearNoteRepository.insertNote(note)
    }

    private fun updateNote(note: Note) = viewModelScope.launch {
        bearNoteRepository.updateNote(note)
    }

    private fun getNewNoteEntry(noteCategoryId: Int, amount: Double, date: Date, information: String?, accountId: Int, id: Int = -1): Note {
        if (id == -1) { // 新记账
            return Note(
                noteCategoryId = noteCategoryId,
                amount = amount,
                date = date,
                information = information,
                accountId = accountId,
                uid = BearNoteApplication.uid,
            )
        } else { // 编辑
            return Note(
                id = id,
                noteCategoryId = noteCategoryId,
                amount = amount,
                date = date,
                information = information,
                accountId = accountId,
                uid = BearNoteApplication.uid,
            )
        }
    }

    // 验证记账的输入数据是否有效
    fun isNoteEntryValid(noteCategoryId: Int, amount: String): Boolean {
        if (noteCategoryId < 0 || TextUtils.isEmpty(amount)) {
            return false
        }
        try {
            if (amount.toDouble() <= .0) {
                return false
            }
        } catch (e: Exception) {
            return false
        }
        return true
    }

    // 存放前已经使用isNoteEntryValid验证数据有效性
    fun addNewNote(amount: Double, date: Date, information: String?, accountId: Int) {
        val newNote = getNewNoteEntry(categoryId, amount, date, information, accountId)
        insertNote(newNote)
    }

    fun updateNote(amount: Double, date: Date, information: String?, accountId: Int) {
        val newNote = getNewNoteEntry(categoryId, amount, date, information, accountId, noteId)
        updateNote(newNote)
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