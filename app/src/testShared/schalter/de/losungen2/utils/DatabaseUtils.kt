package schalter.de.losungen2.utils

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import io.mockk.every
import io.mockk.mockkObject
import schalter.de.losungen2.dataAccess.VersesDatabase

object DatabaseUtils {

    /**
     * You this method only for android >= p in instrumentation tests. For unit test
     * the android version does not matter
     */
    fun mockDatabase() {
        val db: VersesDatabase = getInMemoryDatabase()

        mockkObject(VersesDatabase)
        every { VersesDatabase.provideVerseDatabase(any()) } returns db
    }

    fun getInMemoryDatabase(): VersesDatabase {
        return Room.inMemoryDatabaseBuilder(
                getApplicationContext<Context>(), VersesDatabase::class.java).build()
    }

}