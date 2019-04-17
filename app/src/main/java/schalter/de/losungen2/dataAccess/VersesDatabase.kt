package schalter.de.losungen2.dataAccess

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import schalter.de.losungen2.dataAccess.availableData.AvailableData
import schalter.de.losungen2.dataAccess.availableData.AvailableDataDao
import schalter.de.losungen2.dataAccess.daily.DailyVerse
import schalter.de.losungen2.dataAccess.daily.DailyVersesDao
import schalter.de.losungen2.dataAccess.monthly.MonthlyVerse
import schalter.de.losungen2.dataAccess.monthly.MonthlyVersesDao
import schalter.de.losungen2.dataAccess.sermon.Sermon
import schalter.de.losungen2.dataAccess.weekly.WeeklyVerse
import schalter.de.losungen2.dataAccess.weekly.WeeklyVersesDao

@Database(entities = [DailyVerse::class, WeeklyVerse::class, MonthlyVerse::class, Sermon::class, AvailableData::class], version = 1)
@TypeConverters(Converters::class)
abstract class VersesDatabase : RoomDatabase() {
    abstract fun dailyVerseDao(): DailyVersesDao
    abstract fun weeklyVerseDao(): WeeklyVersesDao
    abstract fun monthlyVerseDao(): MonthlyVersesDao
    abstract fun availableDataDao(): AvailableDataDao

    companion object {
        private var database: VersesDatabase? = null

        fun provideVerseDatabase(context: Context): VersesDatabase {
            database = database ?: Room.databaseBuilder(context, VersesDatabase::class.java, "VersesDatabase").build()
            return database!!
        }
    }
}