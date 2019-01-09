package schalter.de.losungen2.dataAccess.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import schalter.de.losungen2.dataAccess.Language
import schalter.de.losungen2.dataAccess.entities.DailyVerse
import java.util.*

@Dao
abstract class DailyVersesDao {

    fun findDailyVerseByDate(date: Date): LiveData<DailyVerse> = findDailyVerseByExactDate(DailyVerse.getDateForDay(date))

    fun updateLanguage(dailyVerse: DailyVerse) =
            this.updateLanguage(
                    dailyVerse.date,
                    dailyVerse.oldTestamentVerseText,
                    dailyVerse.oldTestamentVerseBible,
                    dailyVerse.newTestamentVerseText,
                    dailyVerse.newTestamentVerseBible,
                    dailyVerse.language)

    fun updateNotes(date: Date, notes: String) = this.updateNotesExactDate(DailyVerse.getDateForDay(date), notes)

    fun updateIsFavourite(date: Date, isFavourite: Boolean) = this.updateIsFavouriteExactDate(DailyVerse.getDateForDay(date), isFavourite)

    @Query("SELECT * FROM DailyVerse WHERE is_favourite IS :isFavourite")
    abstract fun findDailyVersesByFavourite(isFavourite: Boolean = true): LiveData<Array<DailyVerse>>

    @Insert(onConflict = REPLACE)
    abstract fun insertDailyVerse(dailyVerse: DailyVerse)

    @Query("SELECT * FROM DailyVerse WHERE date IS :date")
    protected abstract fun findDailyVerseByExactDate(date: Date): LiveData<DailyVerse>

    @Query("UPDATE DailyVerse SET " +
            "old_testament_verse_text = :oldTestamentVerseText, " +
            "old_testament_verse_bible = :oldTestamentVerseBible, " +
            "new_testament_verse_text = :newTestamentVerseText, " +
            "new_testament_verse_bible = :newTestamentVerseBible, " +
            "language = :language " +
            "WHERE date = :date")
    protected abstract fun updateLanguage(date: Date, oldTestamentVerseText: String, oldTestamentVerseBible: String, newTestamentVerseText: String, newTestamentVerseBible: String, language: Language)

    @Query("UPDATE DailyVerse SET notes = :notes WHERE date = :date")
    protected abstract fun updateNotesExactDate(date: Date, notes: String)

    @Query("UPDATE DailyVerse SET is_favourite = :isFavourite WHERE date = :date")
    protected abstract fun updateIsFavouriteExactDate(date: Date, isFavourite: Boolean)
}