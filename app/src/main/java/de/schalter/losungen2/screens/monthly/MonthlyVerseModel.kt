package de.schalter.losungen2.screens.monthly

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import de.schalter.losungen2.components.verseCard.VerseCardData
import de.schalter.losungen2.dataAccess.VersesDatabase
import de.schalter.losungen2.dataAccess.monthly.MonthlyVerse
import de.schalter.losungen2.dataAccess.weekly.WeeklyVerse
import java.util.*

class MonthlyVerseModel(application: Application, private val database: VersesDatabase, val date: Date) : AndroidViewModel(application) {

    private val monthlyVerseLiveData = loadMonthlyVerse()
    private val weeklyVersesLiveData = loadWeeklyVerses()

    private val verses: MediatorLiveData<List<VerseCardData>> = loadVerses()

    private fun loadMonthlyVerse(): LiveData<MonthlyVerse> {
        return database.monthlyVerseDao().findMonthlyVerseByDate(date)
    }

    private fun loadWeeklyVerses(): LiveData<List<WeeklyVerse>> {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfMonth = calendar.time
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val lastDayOfMonth = calendar.time

        return database.weeklyVerseDao().findWeeklyVerseInDateRange(firstDayOfMonth, lastDayOfMonth)
    }

    private fun loadVerses(): MediatorLiveData<List<VerseCardData>> {
        return MediatorLiveData<List<VerseCardData>>().apply {
            var monthlyVerse: MonthlyVerse? = null
            var weeklyVerses: List<WeeklyVerse> = listOf()

            fun updateData() {
                val verses: MutableList<VerseCardData> = mutableListOf()
                if (monthlyVerse != null) {
                    verses.add(VerseCardData.fromMonthlyVerse(getApplication(), monthlyVerse!!))
                }
                verses.addAll(VerseCardData.fromWeeklyVerses(getApplication(), weeklyVerses))

                value = verses
            }

            addSource(monthlyVerseLiveData) { verse ->
                monthlyVerse = verse
                updateData()
            }

            addSource(weeklyVersesLiveData) { verses ->
                weeklyVerses = verses
                updateData()
            }
        }
    }

    fun getVerses(): MediatorLiveData<List<VerseCardData>> {
        return verses
    }
}

class MonthlyVerseModelFactory(private val application: Application, private val context: Context, private val date: Date) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MonthlyVerseModel(
                application,
                VersesDatabase.provideVerseDatabase(context),
                date) as T
    }

}