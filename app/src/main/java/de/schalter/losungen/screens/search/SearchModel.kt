package de.schalter.losungen.screens.search

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import de.schalter.losungen.R
import de.schalter.losungen.components.verseCard.VerseCardData
import de.schalter.losungen.dataAccess.VersesDatabase
import de.schalter.losungen.dataAccess.daily.DailyVerse
import de.schalter.losungen.dataAccess.monthly.MonthlyVerse
import de.schalter.losungen.dataAccess.weekly.WeeklyVerse

class SearchModel(private val mApplication: Application, private val database: VersesDatabase) : AndroidViewModel(mApplication) {

    private var mediatorLiveData: MediatorLiveData<List<VerseCardData>>? = null

    private var dailyVersesLiveData: LiveData<List<DailyVerse>>? = null
    private var monthlyVersesLiveData: LiveData<List<MonthlyVerse>>? = null
    private var weeklyVersesLiveData: LiveData<List<WeeklyVerse>>? = null

    private var searchWord: String = ""

    fun search(search: String): LiveData<List<VerseCardData>> {
        searchWord = search

        mediatorLiveData?.let { mediatorLiveData ->
            dailyVersesLiveData?.let { mediatorLiveData.removeSource(it) }
            monthlyVersesLiveData?.let { mediatorLiveData.removeSource(it) }
            weeklyVersesLiveData?.let { mediatorLiveData.removeSource(it) }
        }

        dailyVersesLiveData = searchDailyVerses()
        monthlyVersesLiveData = searchMonthlyVerses()
        weeklyVersesLiveData = searchWeeklyVerses()

        return MediatorLiveData<List<VerseCardData>>().apply {
            mediatorLiveData = this

            var dailyVerses: List<VerseCardData> = listOf()
            var monthlyVerses: List<VerseCardData> = listOf()
            var weeklyVerses: List<VerseCardData> = listOf()

            fun updateData(data: List<VerseCardData>, daily: Boolean = false, weekly: Boolean = false, monthly: Boolean = false) {
                val listToSort = mutableListOf<VerseCardData>()
                when {
                    daily -> dailyVerses = data
                    weekly -> weeklyVerses = data
                    monthly -> monthlyVerses = data
                }
                listToSort.addAll(dailyVerses)
                listToSort.addAll(weeklyVerses)
                listToSort.addAll(monthlyVerses)

                sortData(listToSort).apply {
                    value = listToSort
                    postValue(this)
                }
            }

            addSource(dailyVersesLiveData!!) { verses ->
                val verseCardDataList = VerseCardData.fromDailyVerses(mApplication, verses)
                updateData(verseCardDataList, daily = true)
            }

            addSource(weeklyVersesLiveData!!) { verses ->
                val verseCardDataList = VerseCardData.fromWeeklyVerses(mApplication, verses)
                verseCardDataList.forEach { verse ->
                    verse.title = mApplication.getString(R.string.weekly_verse) + " " + verse.title
                }
                updateData(verseCardDataList, weekly = true)
            }

            addSource(monthlyVersesLiveData!!) { verses ->
                val verseCardDataList = VerseCardData.fromMonthlyVerses(mApplication, verses)
                verseCardDataList.forEach { verse ->
                    verse.title += " " + VerseCardData.formateDateOnlyMonthAndYear(verse.date!!)
                }
                updateData(verseCardDataList, monthly = true)
            }
        }
    }

    private fun sortData(data: List<VerseCardData>): List<VerseCardData> {
        return data.sortedBy { verse -> verse.date }
    }

    private fun searchDailyVerses(): LiveData<List<DailyVerse>> {
        return database.dailyVerseDao().searchVerses(searchWord)
    }

    private fun searchWeeklyVerses(): LiveData<List<WeeklyVerse>> {
        return database.weeklyVerseDao().searchVerses(searchWord)
    }

    private fun searchMonthlyVerses(): LiveData<List<MonthlyVerse>> {
        return database.monthlyVerseDao().searchVerses(searchWord)
    }

}

class SearchModelFactory(private val application: Application, private val context: Context) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchModel(application, VersesDatabase.provideVerseDatabase(context)) as T
    }
}