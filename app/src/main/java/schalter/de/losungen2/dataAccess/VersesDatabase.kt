package schalter.de.losungen2.dataAccess

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [DailyVerse::class], version = 1)
@TypeConverters(Converters::class)
abstract class VersesDatabase: RoomDatabase() {
    abstract fun dailyVerseDao(): DailyVersesDao
}