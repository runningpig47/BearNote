package cloud.runningpig.bearnote.logic.utils

import android.content.Context
import cloud.runningpig.bearnote.logic.BearNoteRepository
import cloud.runningpig.bearnote.logic.dao.BearNoteDatabase
import cloud.runningpig.bearnote.ui.note.SpendingViewModelFactory

interface ViewModelFactoryProvider {
    fun providerSpendingViewModelFactory(context: Context): SpendingViewModelFactory
}

val Injector: ViewModelFactoryProvider
    get() = currentInjector

@Volatile
private var currentInjector: ViewModelFactoryProvider =
    DefaultViewModelProvider

private object DefaultViewModelProvider : ViewModelFactoryProvider {

    private fun getBearNoteRepository(context: Context): BearNoteRepository {
        return BearNoteRepository.getInstance(
            noteCategoryDao(context),
            noteDao(context)
        )
    }

    private fun noteCategoryDao(context: Context) =
        BearNoteDatabase.getDatabase(context).noteCategoryDao()

    private fun noteDao(context: Context) =
        BearNoteDatabase.getDatabase(context).noteDao()

    override fun providerSpendingViewModelFactory(context: Context): SpendingViewModelFactory {
        val repository = getBearNoteRepository(context)
        return SpendingViewModelFactory(repository)
    }

}
