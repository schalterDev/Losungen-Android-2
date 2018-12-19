package schalter.de.losungen2.dataAccess

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
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
    @ColumnInfo(name = "date") var date: Date = date
        set(value) { field = getDateForWeek(value) }

    init {
        this.date = getDateForWeek(date)
    }

    fun copy(): WeeklyVerse = WeeklyVerse(weeklyVerseId, date, verseText, verseBible, isFavourite, notes, language)

    companion object {
        fun getDateForWeek(date: Date): Date {
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            calendar.set(Calendar.HOUR_OF_DAY, 12)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            return calendar.time
        }
    }
}