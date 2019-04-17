package schalter.de.losungen2.components.verseCard

import android.content.Context
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import schalter.de.losungen2.R
import schalter.de.losungen2.dataAccess.VersesDatabase
import schalter.de.losungen2.dataAccess.daily.DailyVerse
import schalter.de.losungen2.dataAccess.monthly.MonthlyVerse
import schalter.de.losungen2.dataAccess.weekly.WeeklyVerse
import java.text.SimpleDateFormat
import java.util.*

const val datePattern: String = "dd.MM.yyyy"

data class VerseCardData(
        var title: String,
        var text: String,
        var verse: String,
        var title2: String? = null,
        var text2: String? = null,
        var verse2: String? = null,
        var isFavourite: Boolean = false) {

    var showFavouriteIcon = false
        private set

    var updateIsFavourite: ((isFavourite: Boolean) -> Unit)? = null

    companion object {
        private fun formatDate(date: Date): String {
            // TODO change for multi language
            val dateFormat = SimpleDateFormat(datePattern, Locale.GERMANY)
            return dateFormat.format(date)
        }

        fun fromDailyVerseTwoCards(context: Context, dailyVerse: DailyVerse): List<VerseCardData> {
            val titleNewTestament = context.getString(R.string.new_testament_card_title)
            val titleOldTestament = context.getString(R.string.old_testament_card_title)

            return listOf(
                    VerseCardData(
                            titleOldTestament,
                            dailyVerse.oldTestamentVerseText,
                            dailyVerse.oldTestamentVerseBible,
                            isFavourite = dailyVerse.isFavourite
                    ),
                    VerseCardData(
                            titleNewTestament,
                            dailyVerse.newTestamentVerseText,
                            dailyVerse.newTestamentVerseBible,
                            isFavourite = dailyVerse.isFavourite))
        }

        fun fromDailyVerse(context: Context, dailyVerse: DailyVerse): VerseCardData {
            val date = formatDate(dailyVerse.date)

            val titleNewTestament = context.getString(R.string.new_testament_card_title) + " " + date
            val titleOldTestament = context.getString(R.string.old_testament_card_title) + " " + date

            val data = VerseCardData(
                    titleOldTestament,
                    dailyVerse.oldTestamentVerseText,
                    dailyVerse.oldTestamentVerseBible,
                    titleNewTestament,
                    dailyVerse.newTestamentVerseText,
                    dailyVerse.newTestamentVerseBible,
                    isFavourite = dailyVerse.isFavourite)

            data.showFavouriteIcon = true
            data.updateIsFavourite = { isFavourite ->
                GlobalScope.launch {
                    val database = VersesDatabase.provideVerseDatabase(context)
                    database.dailyVerseDao().updateIsFavourite(dailyVerse.date, isFavourite)
                }
            }
            return data
        }

        fun fromMonthlyVerse(context: Context, monthlyVerse: MonthlyVerse): VerseCardData {
            val titleMonthlyVerse = context.getString(R.string.monthly_verse_title)

            val data = VerseCardData(
                    titleMonthlyVerse,
                    monthlyVerse.verseText,
                    monthlyVerse.verseBible,
                    isFavourite = monthlyVerse.isFavourite
            )

            data.showFavouriteIcon = true
            data.updateIsFavourite = { isFavourite ->
                GlobalScope.launch {
                    val database = VersesDatabase.provideVerseDatabase(context)
                    database.monthlyVerseDao().updateIsFavourite(monthlyVerse.date, isFavourite)
                }
            }

            return data
        }

        private fun fromWeeklyVerse(context: Context, weeklyVerse: WeeklyVerse): VerseCardData {
            val title = this.formatDate(weeklyVerse.date)

            val data = VerseCardData(
                    title,
                    weeklyVerse.verseText,
                    weeklyVerse.verseBible,
                    isFavourite = weeklyVerse.isFavourite
            )

            data.showFavouriteIcon = true
            data.updateIsFavourite = { isFavourite ->
                GlobalScope.launch {
                    val database = VersesDatabase.provideVerseDatabase(context)
                    database.weeklyVerseDao().updateIsFavourite(weeklyVerse.date, isFavourite)
                }
            }
            return data
        }

        fun fromWeeklyVerses(context: Context, weeklyVerses: List<WeeklyVerse>): List<VerseCardData> {
            return weeklyVerses.map { fromWeeklyVerse(context, it) }
        }
    }
}