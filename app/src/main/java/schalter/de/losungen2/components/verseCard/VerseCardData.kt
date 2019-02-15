package schalter.de.losungen2.components.verseCard

import android.content.Context
import schalter.de.losungen2.R
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
        var verse2: String? = null) {
    companion object {
        fun fromDailyVerse(context: Context, dailyVerse: DailyVerse): List<VerseCardData> {
            val titleNewTestament = context.getString(R.string.new_testament_card_title)
            val titleOldTestament = context.getString(R.string.old_testament_card_title)

            return listOf(
                    VerseCardData(
                            titleOldTestament,
                            dailyVerse.oldTestamentVerseText,
                            dailyVerse.oldTestamentVerseBible
                    ),
                    VerseCardData(
                            titleNewTestament,
                            dailyVerse.newTestamentVerseText,
                            dailyVerse.newTestamentVerseBible))
        }

        fun fromMonthlyVerse(context: Context, monthlyVerse: MonthlyVerse): VerseCardData {
            val titleMonthlyVerse = context.getString(R.string.monthly_verse_title)

            return VerseCardData(
                    titleMonthlyVerse,
                    monthlyVerse.verseText,
                    monthlyVerse.verseBible
            )
        }

        fun fromWeeklyVerse(weeklyVerse: WeeklyVerse): VerseCardData {
            // TODO change for multi language
            val dateFormat = SimpleDateFormat(datePattern, Locale.GERMANY)
            val title = dateFormat.format(weeklyVerse.date)

            return VerseCardData(
                    title,
                    weeklyVerse.verseText,
                    weeklyVerse.verseBible
            )
        }

        fun fromWeeklyVerses(weeklyVerses: List<WeeklyVerse>): List<VerseCardData> {
            return weeklyVerses.map { fromWeeklyVerse(it) }
        }
    }
}