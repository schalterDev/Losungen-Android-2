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

    @Query("UPDATE WeeklyVerse SET verse_text = :verseText, verse_bible = :verseBible, language = :language WHERE date = :date")
    protected abstract fun updateLanguage(date: Date, verseText: String, verseBible: String, language: Language)
}