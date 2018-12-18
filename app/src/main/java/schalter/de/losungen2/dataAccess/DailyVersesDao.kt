package schalter.de.losungen2.dataAccess

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import java.util.*

@Dao
abstract class DailyVersesDao {
    @Insert(onConflict = REPLACE)
    abstract fun insertDailyVerse(dailyVerse: DailyVerse)

    @Query("SELECT * FROM DailyVerse WHERE date IS :date")
    abstract fun findDailyVerseByDate(date: Date): DailyVerse

    @Query("SELECT * FROM DailyVerse WHERE is_favourite IS :isFavourite")
    abstract fun findDailyVersesByFavourite(isFavourite: Boolean = true): Array<DailyVerse>

    fun updateLanguage(dailyVerse: DailyVerse) {
        this.updateLanguage(
                dailyVerse.date,
                dailyVerse.oldTestamentVerseText,
                dailyVerse.oldTestamentVerseBible,
                dailyVerse.newTestamentVerseText,
                dailyVerse.newTestamentVerseBible,
                dailyVerse.language)
    }

    @Query("UPDATE DailyVerse SET " +
            "old_testament_verse_text = :oldTestamentVerseText, " +
            "old_testament_verse_bible = :oldTestamentVerseBible, " +
            "new_testament_verse_text = :newTestamentVerseText, " +
            "new_testament_verse_bible = :newTestamentVerseBible, " +
            "language = :language " +
            "WHERE date = :date")
    abstract fun updateLanguage(date: Date, oldTestamentVerseText: String, oldTestamentVerseBible: String, newTestamentVerseText: String, newTestamentVerseBible: String, language: Language)

    @Query("UPDATE DailyVerse SET notes = :notes WHERE date = :date")
    abstract fun updateNotes(date: Date, notes: String)

    @Query("UPDATE DailyVerse SET is_favourite = :isFavourite WHERE date = :date")
    abstract fun updateIsFavourite(date:Date, isFavourite: Boolean)
}