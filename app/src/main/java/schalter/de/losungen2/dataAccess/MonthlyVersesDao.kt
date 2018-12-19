package schalter.de.losungen2.dataAccess

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import java.util.*

@Dao
abstract class MonthlyVersesDao {

    fun findMonthlyVerseByDate(date: Date): MonthlyVerse = findMonthlyVerseByExactDate(MonthlyVerse.getDateForMonth(date))

    fun updateLanguage(monthlyVerse: MonthlyVerse) =
            this.updateLanguage(
                    monthlyVerse.date,
                    monthlyVerse.verseText,
                    monthlyVerse.verseBible,
                    monthlyVerse.language)

    @Insert(onConflict = REPLACE)
    abstract fun insertMonthlyVerse(monthlyVerse: MonthlyVerse)

    @Query("SELECT * FROM MonthlyVerse WHERE date IS :date")
    protected abstract fun findMonthlyVerseByExactDate(date: Date): MonthlyVerse

    @Query("UPDATE MonthlyVerse SET verse_text = :verseText, verse_bible = :verseBible, language = :language WHERE date = :date")
    protected abstract fun updateLanguage(date: Date, verseText: String, verseBible: String, language: Language)
}