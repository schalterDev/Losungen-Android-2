package schalter.de.losungen2.components.dialogs.dateChooser

import android.app.DatePickerDialog
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowDatePickerDialog
import schalter.de.losungen2.utils.TestApplication
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class DateChooserDialogTest {

    private lateinit var datePicker: DatePickerDialog
    private var dateAccepted: Date? = null

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()

        DateChooserDialog(context, Calendar.getInstance()) { dateAccepted = it }

        datePicker = ShadowDatePickerDialog.getLatestDialog() as DatePickerDialog
    }

    @Test
    fun shouldShow() {
        assertNotNull(datePicker)
        assertTrue(datePicker.isShowing)
    }

    @Test
    fun shouldGiveRightDateOnAccept() {
        // verify today is selected
        val today = Calendar.getInstance()
        assertEquals(datePicker.datePicker.year, today.get(Calendar.YEAR))
        assertEquals(datePicker.datePicker.month, today.get(Calendar.MONTH))
        assertEquals(datePicker.datePicker.dayOfMonth, today.get(Calendar.DAY_OF_MONTH))

        datePicker.updateDate(2017, 0, 1)
        datePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).performClick()

        Assert.assertFalse(datePicker.isShowing)

        // verify selected date is returned right
        val calendarAccepted = Calendar.getInstance()
        calendarAccepted.time = dateAccepted
        assertEquals(calendarAccepted.get(Calendar.YEAR), 2017)
        assertEquals(calendarAccepted.get(Calendar.MONTH), 0)
        assertEquals(calendarAccepted.get(Calendar.DAY_OF_MONTH), 1)
    }
}