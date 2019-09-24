package de.schalter.losungen.components.dialogs.deleteSermons

import android.content.Context
import android.os.Looper
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import de.schalter.losungen.R
import de.schalter.losungen.dataAccess.Language
import de.schalter.losungen.dataAccess.daily.DailyVerse
import de.schalter.losungen.dataAccess.sermon.Sermon
import de.schalter.losungen.utils.AsyncTestUtils
import de.schalter.losungen.utils.DatabaseUtils
import de.schalter.losungen.utils.DatabaseUtils.mockSermonDao
import de.schalter.losungen.utils.extensions.clickPositiveButton
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowAlertDialog
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


@RunWith(RobolectricTestRunner::class)
class DeleteSermonsDialogTest {

    private val DATE_FORMAT = "E dd.MMyyyy"

    private lateinit var context: Context
    private lateinit var fragmentManager: FragmentManager
    private lateinit var dialog: AlertDialog

    @Before
    fun setUp() {
        DatabaseUtils.clearMocks()

        context = ApplicationProvider.getApplicationContext()
        context.setTheme(R.style.Theme_Blue)

        AsyncTestUtils.runSingleThread()

        val activity: FragmentActivity = Robolectric.buildActivity(FragmentActivity::class.java).create().start().resume().get()
        activity.setTheme(R.style.Theme_Blue)
        fragmentManager = activity.supportFragmentManager
    }

    private fun showFragment() {
        val fragment = DeleteSermonsDialog(context)
        fragment.show(fragmentManager, "deleteSermonDialog")
        dialog = ShadowAlertDialog.getLatestDialog() as AlertDialog
    }

    private fun mockData(dates: Array<Date> = arrayOf(
            Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 2), // two days before
            Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24), // yesterday
            Date())): Array<DailyVerse> { // today
        val dailyVerses = dates.mapIndexed { index, date ->
            DailyVerse(
                    dailyVerseId = index,
                    date = date,
                    oldTestamentVerseBible = "",
                    newTestamentVerseBible = "",
                    oldTestamentVerseText = "",
                    newTestamentVerseText = "",
                    language = Language.EN
            )
        }

        val dao = DatabaseUtils.mockDailyVersesDao()
        every { dao.getAllDailyVersesWithSermon() } returns dailyVerses.toTypedArray()

        return dailyVerses.toTypedArray()
    }

    private fun getViewAtListPosition(listView: ListView, pos: Int): TextView {
        val firstListItemPosition = listView.firstVisiblePosition
        val lastListItemPosition = firstListItemPosition + listView.childCount - 1

        return if (pos < firstListItemPosition || pos > lastListItemPosition) {
            listView.adapter.getView(pos, null, listView) as TextView
        } else {
            val childIndex = pos - firstListItemPosition
            listView.getChildAt(childIndex) as TextView
        }
    }

    private fun getDateAtListViewPosition(listView: ListView, pos: Int): Date {
        val textView = getViewAtListPosition(listView, pos)

        val dateString = textView.text.toString() + Calendar.getInstance().get(Calendar.YEAR)
        val localDateListEntry = LocalDate.parse(
                dateString,
                DateTimeFormatter.ofPattern(DATE_FORMAT))

        return Date.from(localDateListEntry.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Test
    fun shouldShowDialog() {
        mockData(arrayOf())
        showFragment()
        assertNotNull(dialog)
    }

    @Test
    fun shouldShowRightData() {
        val data = mockData()
        showFragment()
        assertEquals(data.size, dialog.listView.count)

        data.forEachIndexed { index, dailyVerse ->
            assertEquals(dailyVerse.date, DailyVerse.getDateForDay(getDateAtListViewPosition(dialog.listView, index)))
        }
    }

    @Test
    fun shouldShowNoData() {
        val data = mockData(arrayOf())
        showFragment()
        assertEquals(data.size, dialog.listView.count)
    }

    @Test
    fun shouldDeleteSermons() {
        val data = mockData()
        showFragment()

        val sermonDao = mockSermonDao()
        every { sermonDao.deleteSermon(any()) } just Runs

        val sermonLiveData = mutableListOf<MutableLiveData<List<Sermon>>>()
        data.forEach { dailyVerse ->
            val liveData = MutableLiveData<List<Sermon>>()
            every { sermonDao.getSermonsForDate(dailyVerse.date) } returns liveData
            sermonLiveData.add(liveData)
        }

        // click all checkboxes
        for (i in 0 until dialog.listView.count) {
            dialog.listView.performItemClick(
                    dialog.listView.adapter.getView(i, null, null),
                    i,
                    dialog.listView.adapter.getItemId(i)
            )
        }

        dialog.clickPositiveButton()

        sermonLiveData.forEachIndexed { index, liveData ->
            liveData.postValue(listOf(Sermon(dailyVerseId = data[index].dailyVerseId!!, downloadUrl = "", pathSaved = "")))
        }

        // TODO how to remove this sleep
        Thread.sleep(100)
        Shadows.shadowOf(Looper.getMainLooper()).runToEndOfTasks()

        data.forEach { dailyVerse ->
            verify {
                sermonDao.getSermonsForDate(dailyVerse.date)
                sermonDao.deleteSermon(Sermon(dailyVerseId = dailyVerse.dailyVerseId!!, downloadUrl = "", pathSaved = ""))
            }
        }
    }

}