package schalter.de.losungen2.screens.daily

import android.app.DatePickerDialog
import android.content.Context
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragment
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.fakes.RoboMenuItem
import org.robolectric.shadows.ShadowDatePickerDialog
import schalter.de.losungen2.R
import schalter.de.losungen2.dataAccess.daily.DailyVerse
import schalter.de.losungen2.dataAccess.daily.DailyVersesDao
import schalter.de.losungen2.utils.DatabaseUtils.mockDailyVerseDaoFindDailyVerseByDate
import schalter.de.losungen2.utils.DatabaseUtils.mockDailyVersesDao
import schalter.de.losungen2.utils.TestApplication
import java.util.*


@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class DailyVersesOverviewFragmentTest {

    private lateinit var fragmentScenario: FragmentScenario<DailyVersesOverviewFragment>
    private lateinit var context: Context
    private lateinit var dailyVersesDao: DailyVersesDao

    private lateinit var dailyVerseLiveData: MutableLiveData<DailyVerse>

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        dailyVersesDao = mockDailyVersesDao()
        dailyVerseLiveData = mockDailyVerseDaoFindDailyVerseByDate()

        // TODO change this to launchFragmentInContainer when fragment-testing gradle dependencie is out of alpha
        fragmentScenario = launchFragment<DailyVersesOverviewFragment>()
    }

    @Test
    fun clickChooseDate() {
        val item = RoboMenuItem(R.id.action_date)

        fragmentScenario.onFragment {
            it.onOptionsItemSelected(item)
        }

        val datePicker = ShadowDatePickerDialog.getLatestDialog() as DatePickerDialog
        assertNotNull(datePicker)
        assertTrue(datePicker.isShowing)

        // verify today is selected
        val today = Calendar.getInstance()
        assertEquals(datePicker.datePicker.year, today.get(Calendar.YEAR))
        assertEquals(datePicker.datePicker.month, today.get(Calendar.MONTH))
        assertEquals(datePicker.datePicker.dayOfMonth, today.get(Calendar.DAY_OF_MONTH))

        datePicker.updateDate(2017, 0, 1)
        datePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).performClick()

        assertFalse(datePicker.isShowing)

        // TODO is not called but why?
//        verify {
//            dailyVersesDao.findDailyVerseByDate(match {
//                val calendar = Calendar.getInstance()
//                calendar.time = it
//
//                calendar.get(Calendar.YEAR) == 2017 &&
//                        calendar.get(Calendar.MONTH) == 0 &&
//                        calendar.get(Calendar.DAY_OF_MONTH) == 1
//            })
//        }
    }
}