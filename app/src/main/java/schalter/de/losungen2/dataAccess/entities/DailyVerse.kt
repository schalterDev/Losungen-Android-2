package schalter.de.losungen2.dataAccess.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import schalter.de.losungen2.dataAccess.Language
import java.util.*

@Entity
class DailyVerse(
        @ColumnInfo(name = "daily_verse_id")
        @PrimaryKey(autoGenerate = true) var dailyVerseId: Int? = null,
        date: Date,
        @ColumnInfo(name = "old_testament_verse_text") var oldTestamentVerseText: String,
        @ColumnInfo(name = "old_testament_verse_bible") var oldTestamentVerseBible: String,
        @ColumnInfo(name = "new_testament_verse_text") var newTestamentVerseText: String,
        @ColumnInfo(name = "new_testament_verse_bible") var newTestamentVerseBible: String,
        @ColumnInfo(name = "is_favourite") var isFavourite: Boolean = false,
        @ColumnInfo(name = "notes") var notes: String? = null,
        @ColumnInfo(name = "language") var language: Language
) {

    // Date is automatic converted to 12am 0 minutes 0 seconds 0 milliseconds
    @ColumnInfo(name = "date")
    var date: Date = date
        set(value) {
            field = getDateForDay(value)
        }

    init {
        this.date = getDateForDay(date)
    }

    fun copy(): DailyVerse = DailyVerse(dailyVerseId, date, oldTestamentVerseText, oldTestamentVerseBible, newTestamentVerseText, newTestamentVerseBible, isFavourite, notes, language)

    companion object {
        fun getDateForDay(date: Date): Date {
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.set(Calendar.HOUR_OF_DAY, 12)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            return calendar.time
        }
    }
}
