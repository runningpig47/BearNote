package cloud.runningpig.bearnote.ui.note

import androidx.lifecycle.*
import cloud.runningpig.bearnote.BearNoteApplication
import cloud.runningpig.bearnote.BearNoteRepository
import cloud.runningpig.bearnote.logic.model.NoteCategory
import kotlinx.coroutines.launch

class SpendingViewModel(private val bearNoteRepository: BearNoteRepository) : ViewModel() {

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
            recorded = false,
            isUpload = 0
        )
    }

    fun addNewItem(name: String, icon: String, sort: Int, order: Int) {
        val newItem = getNewItemEntry(name, icon, sort, order)
        insert(newItem)
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