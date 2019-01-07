package schalter.de.losungen2.components.verseCard

import android.content.Context
import schalter.de.losungen2.R
import schalter.de.losungen2.dataAccess.DailyVerse
import schalter.de.losungen2.dataAccess.MonthlyVerse

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
    }
}