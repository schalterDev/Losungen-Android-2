package de.schalter.losungen.dataAccess

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import de.schalter.losungen.dataAccess.availableData.AvailableData
import de.schalter.losungen.dataAccess.availableData.AvailableDataDao
import de.schalter.losungen.utils.DatabaseUtils
import de.schalter.losungen.utils.blockingObserve
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AvailableDataDaoUnitTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var availableDataDao: AvailableDataDao
    private lateinit var db: VersesDatabase

    private var availableData = AvailableData(
            language = Language.DE,
            year = 2019
    )

    @Before
    fun createDb() {
        db = DatabaseUtils.getInMemoryDatabase()
        availableDataDao = db.availableDataDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun shouldInsert() {
        availableDataDao.insertAvailableData(availableData)
        var fromDatabase: List<AvailableData>? = availableDataDao.getAvailableDataForLanguage(availableData.language).blockingObserve()

        assertThat(fromDatabase?.size, equalTo(1))
        assertThat(fromDatabase?.get(0), equalTo(availableData))

        availableDataDao.insertAvailableData(availableData)
        availableDataDao.insertAvailableData(availableData.copy(year = 2020))

        fromDatabase = availableDataDao.getAvailableDataForLanguage(availableData.language).blockingObserve()

        assertThat(fromDatabase?.size, equalTo(2))
    }

    @Test
    fun shouldDelete() {
        availableDataDao.insertAvailableData(availableData)
        var fromDatabase: List<AvailableData>? = availableDataDao.getAvailableDataForLanguage(availableData.language).blockingObserve()
        assertThat(fromDatabase?.size, equalTo(1))

        availableDataDao.deleteAvailableData(availableData.language, availableData.year)
        fromDatabase = availableDataDao.getAvailableDataForLanguage(availableData.language).blockingObserve()
        assertThat(fromDatabase?.size, equalTo(0))
    }
}
