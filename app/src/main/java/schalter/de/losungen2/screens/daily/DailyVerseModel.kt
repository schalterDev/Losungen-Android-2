package schalter.de.losungen2.screens.daily

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import schalter.de.losungen2.components.exceptions.DataExceptionWrapper
import schalter.de.losungen2.dataAccess.VersesDatabase
import schalter.de.losungen2.dataAccess.daily.DailyVerse
import schalter.de.losungen2.dataAccess.sermon.Sermon
import schalter.de.losungen2.sermon.SermonProvider
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

    fun toggleFavourite() {
        dailyVerse.value?.let {
            GlobalScope.launch {
                database.dailyVerseDao().updateIsFavourite(
                        it.date,
                        !it.isFavourite)
            }
        }
    }

    /**
     * daily verse has to have a value
     */
    @SuppressLint("CheckResult")
    fun loadSermon(context: Context): LiveData<DataExceptionWrapper<Sermon>> {
        // TODO check if sermon is already in the database

        SermonProvider.getImplementation(context).loadAndSave(dailyVerse.value!!)
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