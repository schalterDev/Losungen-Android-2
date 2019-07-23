package de.schalter.losungen2.screens.favourite

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.schalter.losungen2.R
import de.schalter.losungen2.components.verseCard.VerseCardData
import de.schalter.losungen2.dataAccess.VersesDatabase

class FavouriteVersesModel(private val mApplication: Application, database: VersesDatabase) : AndroidViewModel(mApplication) {

    // for internal use only
    private val dailyVerseFavourite = database.dailyVerseDao().findDailyVersesByFavourite()
    private val weeklyVersesFavourite = database.weeklyVerseDao().findWeeklyVersesByFavourite()
    private val monthlyVersesFavourite = database.monthlyVerseDao().findMonthlyVersesByFavourite()

    private val verses: MediatorLiveData<List<VerseCardData>> = loadVerses()

    private fun loadVerses(): MediatorLiveData<List<VerseCardData>> {
        return MediatorLiveData<List<VerseCardData>>().apply {
            var dailyVerses: List<VerseCardData> = listOf()
            var weeklyVerses: List<VerseCardData> = listOf()
            var monthlyVerses: List<VerseCardData> = listOf()

            fun updateData(data: List<VerseCardData>, daily: Boolean = false, weekly: Boolean = false, monthly: Boolean = false) {
                val listToSort = mutableListOf<VerseCardData>()
                when {
                    daily -> {
                        dailyVerses = data
                    }
                    weekly -> {
                        weeklyVerses = data
                    }
                    monthly -> {
                        monthlyVerses = data
                    }
                }
                listToSort.addAll(dailyVerses)
                listToSort.addAll(weeklyVerses)
                listToSort.addAll(monthlyVerses)

                verses.postValue(sortData(listToSort))
                value = sortData(listToSort)
            }

            addSource(dailyVerseFavourite) { verses ->
                val verseCardDataList = mutableListOf<VerseCardData>()
                verses.forEach { verse -> verseCardDataList.add(VerseCardData.fromDailyVerse(mApplication, verse)) }
                updateData(verseCardDataList, daily = true)
            }

            addSource(weeklyVersesFavourite) { verses ->
                val verseCardDataList = VerseCardData.fromWeeklyVerses(mApplication, verses.toList())
                verseCardDataList.forEach { verse ->
                    verse.title = mApplication.getString(R.string.weekly_verse) + " " + verse.title
                }
                updateData(verseCardDataList, weekly = true)
            }

            addSource(monthlyVersesFavourite) { verses ->
                val verseCardDataList = mutableListOf<VerseCardData>()
                verses.forEach { verse ->
                    val verseToAdd = VerseCardData.fromMonthlyVerse(mApplication, verse)
                    verseToAdd.title += " " + VerseCardData.formateDateOnlyMonthAndYear(verseToAdd.date!!)
                    verseCardDataList.add(verseToAdd)
                }
                updateData(verseCardDataList, monthly = true)
            }
        }
    }

    private fun sortData(data: List<VerseCardData>): List<VerseCardData> {
        return data.sortedBy { verse -> verse.date }
    }

    fun getVerses(): MediatorLiveData<List<VerseCardData>> {
        return verses
    }

}

class FavouriteVersesModelFactory(private val application: Application, private val context: Context) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FavouriteVersesModel(application, VersesDatabase.provideVerseDatabase(context)) as T
    }
}