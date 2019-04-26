package schalter.de.losungen2.screens.daily

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import schalter.de.losungen2.dataAccess.VersesDatabase
import schalter.de.losungen2.dataAccess.daily.DailyVerse
import java.util.*

class DailyVerseModel(private val database: VersesDatabase, private val date: Date) : ViewModel() {

    private val dailyVerse = loadDailyVerse()

    private fun loadDailyVerse(): LiveData<DailyVerse> {
        return database.dailyVerseDao().findDailyVerseByDate(date)
    }

    fun getDailyVerse(): LiveData<DailyVerse> {
        return dailyVerse
    }

    fun toggleFavourite() {
        dailyVerse.value?.let {
            GlobalScope.launch {
                database.dailyVerseDao().updateIsFavourite(
                        it.date,
                        !it.isFavourite)
            }
        }
    }

}

class DailyVerseModelFactory(private val context: Context, private val date: Date) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DailyVerseModel(VersesDatabase.provideVerseDatabase(context), date) as T
    }

}