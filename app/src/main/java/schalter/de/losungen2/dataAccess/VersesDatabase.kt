package schalter.de.losungen2.dataAccess

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import schalter.de.losungen2.dataAccess.daos.AvailableDataDao
import schalter.de.losungen2.dataAccess.daos.DailyVersesDao
import schalter.de.losungen2.dataAccess.daos.MonthlyVersesDao
import schalter.de.losungen2.dataAccess.daos.WeeklyVersesDao
import schalter.de.losungen2.dataAccess.entities.*

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