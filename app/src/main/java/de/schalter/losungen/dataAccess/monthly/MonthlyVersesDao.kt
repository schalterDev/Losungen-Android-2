package de.schalter.losungen.dataAccess.monthly

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import de.schalter.losungen.dataAccess.Language
import java.util.*

@Dao
abstract class MonthlyVersesDao {

    fun findMonthlyVerseByDate(date: Date): LiveData<MonthlyVerse> = findMonthlyVerseByExactDate(MonthlyVerse.getDateForMonth(date))

    fun updateLanguage(monthlyVerse: MonthlyVerse) =
            this.updateLanguage(
                    monthlyVerse.date,
                    monthlyVerse.verseText,
                    monthlyVerse.verseBible,
                    monthlyVerse.language)

    fun updateNotes(date: Date, notes: String) = this.updateNotesExactDate(MonthlyVerse.getDateForMonth(date), notes)

    @Insert(onConflict = REPLACE)
    abstract fun insertMonthlyVerse(monthlyVerse: MonthlyVerse)

    @Query("SELECT * FROM MonthlyVerse WHERE date IS :date")
    protected abstract fun findMonthlyVerseByExactDate(date: Date): LiveData<MonthlyVerse>

    @Query("UPDATE MonthlyVerse SET verse_text = :verseText, verse_bible = :verseBible, language = :language WHERE date = :date")
    protected abstract fun updateLanguage(date: Date, verseText: String, verseBible: String, language: Language)

    fun updateIsFavourite(date: Date, favourite: Boolean) {
        updateIsFavouriteByExactDate(MonthlyVerse.getDateForMonth(date), favourite)
    }

    @Query("SELECT * FROM MonthlyVerse WHERE is_favourite IS :isFavourite")
    abstract fun findMonthlyVersesByFavourite(isFavourite: Boolean = true): LiveData<Array<MonthlyVerse>>

    @Query("UPDATE MonthlyVerse SET is_favourite = :favourite WHERE date = :date")
    protected abstract fun updateIsFavouriteByExactDate(date: Date, favourite: Boolean)

    @Query("SELECT DISTINCT * FROM MonthlyVerse WHERE verse_bible LIKE '%' || :search || '%' OR verse_text LIKE '%' || :search || '%' OR notes LIKE '%' || :search || '%'")
    abstract fun searchVerses(search: String): LiveData<List<MonthlyVerse>>

    @Query("UPDATE MonthlyVerse SET notes = :notes WHERE date = :date")
    protected abstract fun updateNotesExactDate(date: Date, notes: String)
}