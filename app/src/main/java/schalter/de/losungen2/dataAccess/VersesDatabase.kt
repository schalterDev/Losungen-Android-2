package schalter.de.losungen2.dataAccess

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [DailyVerse::class, WeeklyVerse::class, MonthlyVerse::class, Sermon::class], version = 1)
@TypeConverters(Converters::class)
abstract class VersesDatabase: RoomDatabase() {
    abstract fun dailyVerseDao(): DailyVersesDao
    abstract fun weeklyVerseDao(): WeeklyVersesDao
    abstract fun monthlyVerseDao(): MonthlyVersesDao
}