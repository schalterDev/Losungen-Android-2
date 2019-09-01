package de.schalter.losungen.backgroundTasks

import android.content.Context
import android.os.Looper.getMainLooper
import androidx.test.core.app.ApplicationProvider
import com.github.kittinunf.fuel.core.Client
import com.github.kittinunf.fuel.core.Method
import de.schalter.losungen.components.dialogs.importDialog.DataManagement
import de.schalter.losungen.dataAccess.DatabaseHelper
import de.schalter.losungen.dataAccess.Language
import de.schalter.losungen.utils.AsyncUtils
import de.schalter.losungen.utils.DatabaseUtils.mockDatabaseHelper
import de.schalter.losungen.utils.FuelUtils.mockFuel
import de.schalter.losungen.xmlProcessing.LosungenXmlParser
import io.mockk.every
import io.mockk.mockkConstructor
import io.mockk.verify
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.LooperMode
import org.robolectric.annotation.LooperMode.Mode.PAUSED
import org.robolectric.shadows.ShadowProgressDialog
import org.robolectric.shadows.ShadowToast
import java.net.URL
import java.util.concurrent.TimeoutException


@RunWith(RobolectricTestRunner::class)
@LooperMode(PAUSED)
class ImportVersesTaskTest {

    lateinit var context: Context
    lateinit var fuelMockClient: Client

    private val url = "https://validurl.com"
    private val validData = listOf(DataManagement.YearLanguageUrl(
            2019,
            Language.DE,
            url,
            fileNameDailyVerses = "2019_en.xml",
            fileNameMonthlyVerses = "2019_en_month.xml",
            fileNameWeeklyVerses = "2019_en_week.xml"))

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        fuelMockClient = mockFuel()
    }

    private fun mockNetworkError() {
        every { fuelMockClient.executeRequest(any()) } throws TimeoutException()
    }

    private fun mockWithRealData() {
        val fileInputStream = this.javaClass.classLoader?.getResourceAsStream("2019_en.zip")

        every { fuelMockClient.executeRequest(any()).statusCode } returns 200
        every { fuelMockClient.executeRequest(any()).dataStream } returns fileInputStream!!

        mockkConstructor(LosungenXmlParser::class)
        every { anyConstructed<LosungenXmlParser>().parseVerseXml(any()) } returns listOf()
        mockDatabaseHelper()
    }

    @Test
    fun shouldRunBackgroundTask() {
        val asyncTask = ImportVersesTask(context)
        asyncTask.execute(listOf())

        shadowOf(getMainLooper()).idle()
    }

    @Test
    fun shouldSendNetworkRequests() {
        val asyncTask = ImportVersesTask(context)
        asyncTask.execute(validData)

        shadowOf(getMainLooper()).idle()

        verify(exactly = 1) {
            fuelMockClient.executeRequest(
                    withArg {
                        assertEquals(Method.GET, it.method)
                        assertEquals(URL(url), it.url)
                    }
            )
        }
    }

    @Test
    fun shouldShowToastOnNetworkError() {
        mockNetworkError()

        val asyncTask = ImportVersesTask(context)
        asyncTask.execute(validData)

        shadowOf(getMainLooper()).idle()

        assertEquals(1, ShadowToast.shownToastCount())
    }

    @Test
    fun shouldDownloadFile() {
        AsyncUtils.runSingleThread()
        mockWithRealData()

        val asyncTask = ImportVersesTask(context)
        asyncTask.execute(validData)

        shadowOf(getMainLooper()).runToEndOfTasks()

        verify(exactly = 1) { anyConstructed<DatabaseHelper>().importDailyVerses(any()) }
        verify(exactly = 1) { anyConstructed<DatabaseHelper>().importWeeklyVerses(any()) }
        verify(exactly = 1) { anyConstructed<DatabaseHelper>().importMonthlyVerses(any()) }
    }

    @Test
    fun shouldShowAndCloseDialogWithRightData() {
        mockWithRealData()

        val asyncTask = ImportVersesTask(context)
        asyncTask.execute(validData)

        val dialog = ShadowProgressDialog.getLatestDialog()
        Assert.assertNotNull(dialog)
        Assert.assertTrue(dialog.isShowing)

        shadowOf(getMainLooper()).idle()
        Assert.assertFalse(dialog.isShowing)
    }

    @Test
    fun shouldShowAndCloseDialogWhenError() {
        mockNetworkError()

        val asyncTask = ImportVersesTask(context)
        asyncTask.execute(validData)

        val dialog = ShadowProgressDialog.getLatestDialog()
        Assert.assertNotNull(dialog)
        Assert.assertTrue(dialog.isShowing)

        shadowOf(getMainLooper()).idle()
        Assert.assertFalse(dialog.isShowing)
    }

}