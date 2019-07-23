package de.schalter.losungen.utils

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import io.mockk.every
import io.mockk.mockkClass
import io.mockk.mockkObject
import de.schalter.losungen.dataAccess.VersesDatabase
import de.schalter.losungen.dataAccess.daily.DailyVerse
import de.schalter.losungen.dataAccess.daily.DailyVersesDao

object DatabaseUtils {

    private var database: VersesDatabase? = null
    private var dailyVersesDao: DailyVersesDao? = null
    private var dailyVerseLiveData = MutableLiveData<DailyVerse>()

    fun getInMemoryDatabase(): VersesDatabase {
        return Room.inMemoryDatabaseBuilder(
                getApplicationContext<Context>(), VersesDatabase::class.java).build()
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

        return dailyVerseLiveData
    }

}