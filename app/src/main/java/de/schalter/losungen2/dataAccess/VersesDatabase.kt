package de.schalter.losungen2.dataAccess

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.schalter.losungen2.dataAccess.availableData.AvailableData
import de.schalter.losungen2.dataAccess.availableData.AvailableDataDao
import de.schalter.losungen2.dataAccess.daily.DailyVerse
import de.schalter.losungen2.dataAccess.daily.DailyVersesDao
import de.schalter.losungen2.dataAccess.monthly.MonthlyVerse
import de.schalter.losungen2.dataAccess.monthly.MonthlyVersesDao
import de.schalter.losungen2.dataAccess.sermon.Sermon
import de.schalter.losungen2.dataAccess.sermon.SermonDao
import de.schalter.losungen2.dataAccess.weekly.WeeklyVerse
import de.schalter.losungen2.dataAccess.weekly.WeeklyVersesDao

@Database(entities = [DailyVerse::class, WeeklyVerse::class, MonthlyVerse::class, Sermon::class, AvailableData::class], version = 1)
@TypeConverters(Converters::class)
abstract class VersesDatabase : RoomDatabase() {
    abstract fun dailyVerseDao(): DailyVersesDao
    abstract fun weeklyVerseDao(): WeeklyVersesDao
    abstract fun monthlyVerseDao(): MonthlyVersesDao
    abstract fun availableDataDao(): AvailableDataDao
    abstract fun sermonDao(): SermonDao

    companion object {
        private var database: VersesDatabase? = null

        fun provideVerseDatabase(context: Context): VersesDatabase {
            database = database
                    ?: Room.databaseBuilder(context, VersesDatabase::class.java, "VersesDatabase").build()
            return database!!
        }
    }
}