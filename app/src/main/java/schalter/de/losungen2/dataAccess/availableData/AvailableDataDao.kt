package schalter.de.losungen2.dataAccess.availableData

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import schalter.de.losungen2.dataAccess.Language

@Dao
interface AvailableDataDao {
    @Insert(onConflict = REPLACE)
    fun insertAvailableData(availableData: AvailableData)

    @Query("SELECT * FROM AvailableData WHERE language = :language")
    fun getAvailableDataForLanguage(language: Language): LiveData<List<AvailableData>>

    @Query("DELETE FROM AvailableData WHERE language = :language AND year = :year")
    fun deleteAvailableData(language: Language, year: Int)
}