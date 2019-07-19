package schalter.de.losungen2.components.verseCard

import android.app.Application
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import schalter.de.losungen2.R
import schalter.de.losungen2.dataAccess.VersesDatabase
import schalter.de.losungen2.dataAccess.daily.DailyVerse
import schalter.de.losungen2.dataAccess.monthly.MonthlyVerse
import schalter.de.losungen2.dataAccess.weekly.WeeklyVerse
import schalter.de.losungen2.utils.LanguageUtils
import java.text.SimpleDateFormat
import java.util.*

const val datePattern: String = "dd.MM.yyyy"
const val datePatternMonthYear: String = "MMM yy"

data class VerseCardData(
        var title: String,
        var text: String,
        var verse: String,
        var title2: String? = null,
        var text2: String? = null,
        var verse2: String? = null,
        var isFavourite: Boolean = false,
        var date: Date? = null) {

    var showFavouriteIcon = false
        private set

    var updateIsFavourite: ((isFavourite: Boolean) -> Unit)? = null

    companion object {
        private val locale = LanguageUtils.getDisplayLanguageLocale();

        private fun formatDate(date: Date): String {
            val dateFormat = SimpleDateFormat(datePattern, locale)
            return dateFormat.format(date)
        }

        fun formateDateOnlyMonthAndYear(date: Date): String {
            val dateFormat = SimpleDateFormat(datePatternMonthYear, locale)
            return dateFormat.format(date)
        }

        fun fromDailyVerseTwoCards(application: Application, dailyVerse: DailyVerse): List<VerseCardData> {
            val titleNewTestament = application.getString(R.string.new_testament_card_title)
            val titleOldTestament = application.getString(R.string.old_testament_card_title)

            return listOf(
                    VerseCardData(
                            titleOldTestament,
                            dailyVerse.oldTestamentVerseText,
                            dailyVerse.oldTestamentVerseBible,
                            isFavourite = dailyVerse.isFavourite,
                            date = dailyVerse.date
                    ),
                    VerseCardData(
                            titleNewTestament,
                            dailyVerse.newTestamentVerseText,
                            dailyVerse.newTestamentVerseBible,
                            isFavourite = dailyVerse.isFavourite,
                            date = dailyVerse.date))
        }

        fun fromDailyVerses(application: Application, dailyVerses: List<DailyVerse>): List<VerseCardData> {
            return dailyVerses.map { fromDailyVerse(application, it) }
        }

        fun fromDailyVerse(application: Application, dailyVerse: DailyVerse): VerseCardData {
            val date = formatDate(dailyVerse.date)

            val titleNewTestament = application.getString(R.string.new_testament_card_title) + " " + date
            val titleOldTestament = application.getString(R.string.old_testament_card_title) + " " + date

            val data = VerseCardData(
                    titleOldTestament,
                    dailyVerse.oldTestamentVerseText,
                    dailyVerse.oldTestamentVerseBible,
                    titleNewTestament,
                    dailyVerse.newTestamentVerseText,
                    dailyVerse.newTestamentVerseBible,
                    isFavourite = dailyVerse.isFavourite,
                    date = dailyVerse.date)

            data.showFavouriteIcon = true
            data.updateIsFavourite = { isFavourite ->
                GlobalScope.launch {
                    val database = VersesDatabase.provideVerseDatabase(application)
                    database.dailyVerseDao().updateIsFavourite(dailyVerse.date, isFavourite)
                }
            }
            return data
        }

        fun fromMonthlyVerses(application: Application, monthlyVerses: List<MonthlyVerse>): List<VerseCardData> {
            return monthlyVerses.map { fromMonthlyVerse(application, it) }
        }

        fun fromMonthlyVerse(application: Application, monthlyVerse: MonthlyVerse): VerseCardData {
            val titleMonthlyVerse = application.getString(R.string.monthly_verse_title)

            val data = VerseCardData(
                    titleMonthlyVerse,
                    monthlyVerse.verseText,
                    monthlyVerse.verseBible,
                    isFavourite = monthlyVerse.isFavourite,
                    date = monthlyVerse.date
            )

            data.showFavouriteIcon = true
            data.updateIsFavourite = { isFavourite ->
                GlobalScope.launch {
                    val database = VersesDatabase.provideVerseDatabase(application)
                    database.monthlyVerseDao().updateIsFavourite(monthlyVerse.date, isFavourite)
                }
            }

            return data
        }

        private fun fromWeeklyVerse(application: Application, weeklyVerse: WeeklyVerse): VerseCardData {
            val title = this.formatDate(weeklyVerse.date)

            val data = VerseCardData(
                    title,
                    weeklyVerse.verseText,
                    weeklyVerse.verseBible,
                    isFavourite = weeklyVerse.isFavourite,
                    date = weeklyVerse.date
            )

            data.showFavouriteIcon = true
            data.updateIsFavourite = { isFavourite ->
                GlobalScope.launch {
                    val database = VersesDatabase.provideVerseDatabase(application)
                    database.weeklyVerseDao().updateIsFavourite(weeklyVerse.date, isFavourite)
                }
            }
            return data
        }

        fun fromWeeklyVerses(application: Application, weeklyVerses: List<WeeklyVerse>): List<VerseCardData> {
            return weeklyVerses.map { fromWeeklyVerse(application, it) }
        }
    }
}