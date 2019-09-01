package de.schalter.losungen.utils

import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import de.schalter.losungen.dataAccess.DatabaseHelper
import de.schalter.losungen.dataAccess.VersesDatabase
import de.schalter.losungen.dataAccess.daily.DailyVerse
import de.schalter.losungen.dataAccess.daily.DailyVersesDao
import io.mockk.*

object DatabaseUtils {

    private var database: VersesDatabase? = null
    private var dailyVersesDao: DailyVersesDao? = null
    private var dailyVerseLiveData = MutableLiveData<DailyVerse>()

    fun getInMemoryDatabase(): VersesDatabase {
        return Room.inMemoryDatabaseBuilder(
                getApplicationContext(), VersesDatabase::class.java).build()
    }

    fun mockDailyVersesDao(): DailyVersesDao {
        if (database == null) {
            database = mockkClass(VersesDatabase::class)
        }
        if (dailyVersesDao == null) {
            dailyVersesDao = mockkClass(DailyVersesDao::class)
        }
        every { database!!.dailyVerseDao() } returns dailyVersesDao!!

        mockkObject(VersesDatabase)
        every { VersesDatabase.provideVerseDatabase(any()) } returns database!!

        return dailyVersesDao!!
    }

    fun mockDailyVerseDaoFindDailyVerseByDate(): MutableLiveData<DailyVerse> {
        val dao = mockDailyVersesDao()
        every { dao.findDailyVerseByDate(any()) } returns dailyVerseLiveData

        every { dao.updateNotes(any(), any()) } just Runs

        return dailyVerseLiveData
    }

    fun mockDatabaseHelper() {
        mockkConstructor(DatabaseHelper::class)
        every { anyConstructed<DatabaseHelper>().importDailyVerses(any()) } returns Unit
        every { anyConstructed<DatabaseHelper>().importWeeklyVerses(any()) } returns Unit
        every { anyConstructed<DatabaseHelper>().importMonthlyVerses(any()) } returns Unit
    }

}