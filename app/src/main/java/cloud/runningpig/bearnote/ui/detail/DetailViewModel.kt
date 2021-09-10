package cloud.runningpig.bearnote.ui.detail

import android.util.Log
import androidx.lifecycle.*
import cloud.runningpig.bearnote.BearNoteRepository
import cloud.runningpig.bearnote.logic.model.NoteDetail
import java.util.*

class DetailViewModel(private val bearNoteRepository: BearNoteRepository) : ViewModel() {
    val date = MutableLiveData(Date())

    val queryByDate: LiveData<List<NoteDetail>> = Transformations.switchMap(date) {
        val startOfDay = getStartOfDay(it)
        val endOfDay = getEndOfDay(it)
        bearNoteRepository.queryByDate(startOfDay, endOfDay).asLiveData()
    }

    /** 设置参数Date到当天00:00:00 */
    private fun getStartOfDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
//        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(calendar.time)
        return calendar.time
    }

    /** 设置参数Date到当天23:59:59 */
    private fun getEndOfDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
//        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(calendar.time)
        return calendar.time
    }

}

class DetailViewModelFactory(private val bearNoteRepository: BearNoteRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(bearNoteRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}