package de.schalter.losungen.migration

import de.schalter.losungen.dataAccess.Language
import de.schalter.losungen.dataAccess.daily.DailyVerse
import de.schalter.losungen.dataAccess.monthly.MonthlyVerse
import de.schalter.losungen.dataAccess.weekly.WeeklyVerse
import java.util.*

/**
 * copied from legacy project and added the method
 */
class LegacyLosung {
    var losungstext: String? = null
    var losungsvers: String? = null
    var lehrtext: String? = null
    var lehrtextVers: String? = null
    var sundayName: String? = null
    var date: Long = 0
    var isMarked: Boolean = false
    var notesLehrtext: String? = null
    var notesLosung: String? = null
    var audioPath: String? = null

    fun toDailyVerse(language: String): DailyVerse {
        return DailyVerse(
                date = Date(this.date),
                oldTestamentVerseText = this.losungstext ?: "",
                oldTestamentVerseBible = this.losungsvers ?: "",
                newTestamentVerseText = this.lehrtext ?: "",
                newTestamentVerseBible = this.lehrtextVers ?: "",
                isFavourite = this.isMarked,
                notes = this.notesLosung,
                language = Language.fromString(language) ?: Language.DE
        )
    }

    fun toWeeklyVerse(language: String): WeeklyVerse {
        return WeeklyVerse(
                date = Date(this.date),
                verseText = this.losungstext ?: "",
                verseBible = this.losungsvers ?: "",
                isFavourite = this.isMarked,
                language = Language.fromString(language) ?: Language.DE
        )
    }

    fun toMonthlyVerse(language: String): MonthlyVerse {
        return MonthlyVerse(
                date = Date(this.date),
                verseText = this.losungstext ?: "",
                verseBible = this.losungsvers ?: "",
                isFavourite = this.isMarked,
                notes = this.notesLosung,
                language = Language.fromString(language) ?: Language.DE
        )
    }
}