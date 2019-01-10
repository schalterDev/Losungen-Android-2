package schalter.de.losungen2.dataAccess

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import schalter.de.losungen2.dataAccess.availableData.AvailableData
import schalter.de.losungen2.dataAccess.availableData.AvailableDataDao
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

private fun <T> LiveData<T>.blockingObserve(): T? {
    var value: T? = null
    val latch = CountDownLatch(1)

    observeForever { t ->
        value = t
        latch.countDown()
    }

    latch.await(2, TimeUnit.SECONDS)
    return value
}

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
        val context: Context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
                context, VersesDatabase::class.java).build()
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
