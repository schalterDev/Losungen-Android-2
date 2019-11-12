package de.schalter.losungen.dataAccess.weekly

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import de.schalter.losungen.dataAccess.Language
import java.util.*

@Entity(indices = [
//    Index(value = ["date", "language"], unique = true),
    Index(value = ["date"], unique = true),
    Index(value = ["weekly_verse_id"])
])
class WeeklyVerse(
        @ColumnInfo(name = "weekly_verse_id")
        @PrimaryKey(autoGenerate = true) var weeklyVerseId: Int? = null,
        date: Date,
        @ColumnInfo(name = "verse_text") var verseText: String,
        @ColumnInfo(name = "verse_bible") var verseBible: String,
        @ColumnInfo(name = "is_favourite") var isFavourite: Boolean = false,
        @ColumnInfo(name = "notes") var notes: String? = null,
        @ColumnInfo(name = "language") var language: Language
) {

    // Date is automatic onverted to Monday 12 am 0m 0s 0ms
    @ColumnInfo(name = "date")
    var date: Date = date
        set(value) {
            field = getDateForWeek(value)
        }

    init {
        this.date = getDateForWeek(date)
    }

    fun copy(): WeeklyVerse = WeeklyVerse(weeklyVerseId, date, verseText, verseBible, isFavourite, notes, language)

    companion object {
        fun getDateForWeek(date: Date): Date {
            val calendarOwnTimeZone = Calendar.getInstance()
            calendarOwnTimeZone.time = date

            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.firstDayOfWeek = Calendar.SUNDAY
            calendar.set(
                    calendarOwnTimeZone.get(Calendar.YEAR),
                    calendarOwnTimeZone.get(Calendar.MONTH),
                    calendarOwnTimeZone.get(Calendar.DATE),
                    12,
                    0,
                    0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.get(Calendar.DAY_OF_YEAR)
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

            return calendar.time
        }
    }
}