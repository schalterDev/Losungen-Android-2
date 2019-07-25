package de.schalter.losungen.screens.daily

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.schalter.losungen.components.exceptions.DataExceptionWrapper
import de.schalter.losungen.dataAccess.VersesDatabase
import de.schalter.losungen.dataAccess.daily.DailyVerse
import de.schalter.losungen.dataAccess.sermon.Sermon
import de.schalter.losungen.sermon.sermonProvider.SermonProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class DailyVerseModel(private val database: VersesDatabase, private val date: Date) : ViewModel() {

    private val dailyVerse = loadDailyVerse()
    private var sermon: MutableLiveData<DataExceptionWrapper<Sermon>> = MutableLiveData()

    private fun loadDailyVerse(): LiveData<DailyVerse> {
        return database.dailyVerseDao().findDailyVerseByDate(date)
    }

    fun getDailyVerse(): LiveData<DailyVerse> {
        return dailyVerse
    }

    fun sermonProviderForLanguageAvailable(context: Context): Boolean {
        return SermonProvider.implementationForLanguageAvailable(context)
    }

    fun sermonAvailable(): LiveData<List<Sermon>> {
        return database.sermonDao().getSermonsForDate(DailyVerse.getDateForDay(date))
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

    fun saveNotes(notes: String) {
        dailyVerse.value?.let {
            if (notes != it.notes) {
                GlobalScope.launch {
                    database.dailyVerseDao().updateNotes(it.date, notes)
                }
            }
        }
    }

    /**
     * daily verse has to have a value
     */
    @SuppressLint("CheckResult")
    fun loadSermon(context: Context): LiveData<DataExceptionWrapper<Sermon>> {
        sermon = MutableLiveData()
        SermonProvider.getImplementation(context).getIfExistsOrLoadAndSave(dailyVerse.value!!.date)
                .subscribe(
                        { success -> sermon.postValue(DataExceptionWrapper(value = success)) },
                        { error -> sermon.postValue(DataExceptionWrapper(error = error)) })

        return sermon
    }

    fun getSermon() = sermon
}

class DailyVerseModelFactory(private val context: Context, private val date: Date) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DailyVerseModel(VersesDatabase.provideVerseDatabase(context), date) as T
    }

}