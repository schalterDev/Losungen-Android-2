package schalter.de.losungen2.dataAccess

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [DailyVerse::class, WeeklyVerse::class, MonthlyVerse::class, Sermon::class, AvailableData::class], version = 1)
@TypeConverters(Converters::class)
abstract class VersesDatabase : RoomDatabase() {
    abstract fun dailyVerseDao(): DailyVersesDao
    abstract fun weeklyVerseDao(): WeeklyVersesDao
    abstract fun monthlyVerseDao(): MonthlyVersesDao
    abstract fun availableDataDao(): AvailableDataDao

    companion object {
        fun provideVerseDatabase(context: Context): VersesDatabase {
            return Room.databaseBuilder(context, VersesDatabase::class.java, "VersesDatabase").build()
        }
    }
}