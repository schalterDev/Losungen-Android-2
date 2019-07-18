package schalter.de.losungen2.dataAccess.sermon

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import java.util.*

@Dao
abstract class SermonDao {

    @Insert(onConflict = REPLACE)
    abstract fun insertSermon(sermon: Sermon)

    @Update
    abstract fun updateSermon(sermon: Sermon)

    @Delete
    abstract fun deleteSermon(sermon: Sermon)

    @Query("SELECT * FROM Sermon INNER JOIN DailyVerse on Sermon.daily_verse_id = DailyVerse.daily_verse_id WHERE DailyVerse.date = :date")
    abstract fun getSermonsForDate(date: Date): LiveData<List<Sermon>>

    @Query("SELECT * FROM Sermon WHERE daily_verse_id IS :dailyVerseId")
    abstract fun getSermonsForDailyVerseId(dailyVerseId: Int): LiveData<List<Sermon>>

    @Query("SELECT * FROM Sermon INNER JOIN DailyVerse on Sermon.daily_verse_id = DailyVerse.daily_verse_id WHERE DailyVerse.date < :date")
    abstract fun getSermonsBeforeDate(date: Date): LiveData<List<Sermon>>
}
