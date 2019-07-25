package de.schalter.losungen.components.verseCard

import android.app.Application
import de.schalter.losungen.R
import de.schalter.losungen.dataAccess.daily.DailyVerse
import de.schalter.losungen.dataAccess.monthly.MonthlyVerse
import de.schalter.losungen.dataAccess.weekly.WeeklyVerse
import de.schalter.losungen.utils.LanguageUtils
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
        var date: Date? = null,
        var notes: String? = null,
        var type: Type) {

    var showFavouriteIcon = false
        private set

    companion object {
        private val locale = LanguageUtils.getDisplayLanguageLocale()

        private fun formatDate(date: Date): String {
            val dateFormat = SimpleDateFormat(datePattern, locale)
            return dateFormat.format(date)
        }

        fun formatDateOnlyMonthAndYear(date: Date): String {
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
                            date = dailyVerse.date,
                            type = Type.DAILY
                    ),
                    VerseCardData(
                            titleNewTestament,
                            dailyVerse.newTestamentVerseText,
                            dailyVerse.newTestamentVerseBible,
                            isFavourite = dailyVerse.isFavourite,
                            date = dailyVerse.date,
                            type = Type.DAILY))
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
                    date = dailyVerse.date,
                    notes = dailyVerse.notes,
                    type = Type.DAILY)

            data.showFavouriteIcon = true
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
                    date = monthlyVerse.date,
                    notes = monthlyVerse.notes,
                    type = Type.MONTHLY
            )

            data.showFavouriteIcon = true
            return data
        }

        private fun fromWeeklyVerse(application: Application, weeklyVerse: WeeklyVerse): VerseCardData {
            val title = this.formatDate(weeklyVerse.date)

            val data = VerseCardData(
                    title,
                    weeklyVerse.verseText,
                    weeklyVerse.verseBible,
                    isFavourite = weeklyVerse.isFavourite,
                    date = weeklyVerse.date,
                    notes = weeklyVerse.notes,
                    type = Type.WEEKLY
            )

            data.showFavouriteIcon = true
            return data
        }

        fun fromWeeklyVerses(application: Application, weeklyVerses: List<WeeklyVerse>): List<VerseCardData> {
            return weeklyVerses.map { fromWeeklyVerse(application, it) }
        }
    }

    enum class Type {
        DAILY, WEEKLY, MONTHLY
    }
}