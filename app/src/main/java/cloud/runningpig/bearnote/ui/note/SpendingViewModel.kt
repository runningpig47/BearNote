package cloud.runningpig.bearnote.ui.note

import androidx.lifecycle.*
import cloud.runningpig.bearnote.BearNoteRepository
import cloud.runningpig.bearnote.logic.model.NoteCategory

class SpendingViewModel(private val bearNoteRepository: BearNoteRepository) : ViewModel() {

    fun loadBySort(sort: Int) = bearNoteRepository.loadBySort(sort).asLiveData()

    val page = MutableLiveData<Int>(0)
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