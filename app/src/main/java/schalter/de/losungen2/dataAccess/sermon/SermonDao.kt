package schalter.de.losungen2.dataAccess.sermon

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update

@Dao
abstract class SermonDao {

    @Insert(onConflict = REPLACE)
    protected abstract fun insertSermon(sermon: Sermon)

    @Update
    protected abstract fun updateSermon(sermon: Sermon)

    @Query("SELECT * FROM Sermon WHERE daily_verse_id IS :dailyVerseId")
    protected abstract fun getSermonsForDailyVerseId(dailyVerseId: Int): LiveData<List<Sermon>>
}
