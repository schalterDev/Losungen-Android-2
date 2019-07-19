package schalter.de.losungen2.dataAccess.weekly

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import schalter.de.losungen2.dataAccess.Language
import java.util.*

@Dao
abstract class WeeklyVersesDao {

    fun findWeeklyVerseByDate(date: Date): LiveData<WeeklyVerse> = findWeeklyVerseByExactDate(WeeklyVerse.getDateForWeek(date))

    fun findWeeklyVerseInDateRange(start: Date, end: Date): LiveData<List<WeeklyVerse>> {
        val calendar = Calendar.getInstance()
        calendar.time = start
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.HOUR, 0)
        val startConverted = calendar.time

        calendar.time = end
        calendar.set(Calendar.MILLISECOND, 999)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.HOUR, 23)
        val endConverted = calendar.time

        return this.findWeeklyVersesInExactDateRange(startConverted, endConverted)
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

    fun updateIsFavourite(date: Date, favourite: Boolean) {
        updateIsFavouriteByExactDate(WeeklyVerse.getDateForWeek(date), favourite)
    }

    @Query("SELECT * FROM WeeklyVerse WHERE is_favourite IS :isFavourite")
    abstract fun findWeeklyVersesByFavourite(isFavourite: Boolean = true): LiveData<Array<WeeklyVerse>>

    @Query("UPDATE WeeklyVerse SET is_favourite = :favourite WHERE date = :date")
    protected abstract fun updateIsFavouriteByExactDate(date: Date, favourite: Boolean)

    @Query("SELECT DISTINCT * FROM WeeklyVerse WHERE verse_bible LIKE '%' || :search || '%' OR verse_text LIKE '%' || :search || '%'")
    abstract fun searchVerses(search: String): LiveData<List<WeeklyVerse>>
}