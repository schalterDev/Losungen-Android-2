package schalter.de.losungen2.dataAccess

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import java.util.*

@Dao
abstract class WeeklyVersesDao {

    fun findWeeklyVerseByDate(date: Date): LiveData<WeeklyVerse> = findWeeklyVerseByExactDate(WeeklyVerse.getDateForWeek(date))

    fun findWeeklyVerseInDateRange(start: Date, end: Date): LiveData<List<WeeklyVerse>> {
        return this.findWeeklyVersesInExactDateRange(WeeklyVerse.getDateForWeek(start), WeeklyVerse.getDateForWeek(end))
    }

    fun updateLanguage(weeklyVerse: WeeklyVerse) =
            this.updateLanguage(
                    weeklyVerse.date,
                    weeklyVerse.verseText,
                    weeklyVerse.verseBible,
                    weeklyVerse.language)

    @Insert(onConflict = REPLACE)
    abstract fun insertWeeklyVerse(weeklyVerse: WeeklyVerse)

    @Query("SELECT * FROM WeeklyVerse WHERE date IS :date")
    protected abstract fun findWeeklyVerseByExactDate(date: Date): LiveData<WeeklyVerse>

    @Query("SELECT * FROM WeeklyVerse WHERE date >= :start AND date <= :end")
    protected abstract fun findWeeklyVersesInExactDateRange(start: Date, end: Date): LiveData<List<WeeklyVerse>>

    @Query("UPDATE WeeklyVerse SET verse_text = :verseText, verse_bible = :verseBible, language = :language WHERE date = :date")
    protected abstract fun updateLanguage(date: Date, verseText: String, verseBible: String, language: Language)
}