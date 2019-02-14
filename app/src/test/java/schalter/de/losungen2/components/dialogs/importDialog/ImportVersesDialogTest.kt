package schalter.de.losungen2.components.dialogs.importDialog

import android.app.Application
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import io.mockk.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowDialog
import schalter.de.losungen2.R
import schalter.de.losungen2.backgroundTasks.ImportVersesTask
import schalter.de.losungen2.components.emptyState.EmptyStateView
import schalter.de.losungen2.dataAccess.Language
import schalter.de.losungen2.utils.CoroutineUtils
import schalter.de.losungen2.utils.TestApplication


@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class ImportVersesDialogTest {

    private lateinit var context: Application
    private lateinit var alertDialog: AlertDialog

    private var liveDataDataManagement = MutableLiveData<List<DataManagement.YearLanguageUrl>>()

    private val testData = listOf(
            DataManagement.YearLanguageUrl(2017, Language.DE, "", copyrightUrl = "http://german.terms"),
            DataManagement.YearLanguageUrl(2018, Language.DE, "", copyrightUrl = "http://german.terms"),
            DataManagement.YearLanguageUrl(2019, Language.DE, "", copyrightUrl = "http://german2.terms"),
            DataManagement.YearLanguageUrl(2018, Language.EN, "", copyrightUrl = "http://english.terms"),
            DataManagement.YearLanguageUrl(2019, Language.EN, "")
    )

    @Before
    fun loadDialog() {
        CoroutineUtils.runSingleThread()
        mockDataManagement()
        context = ApplicationProvider.getApplicationContext()

        val fragment = ImportVersesDialog()
        val activity: FragmentActivity = Robolectric.buildActivity(FragmentActivity::class.java).create().start().resume().get()
        val fragmentManager = activity.supportFragmentManager
        fragment.show(fragmentManager, "importDialog")

        this.loadDialogInstance()
    }

    private fun mockDataManagement() {
        mockkConstructor(DataManagement::class)

        every { anyConstructed<DataManagement>().getAvailableData() } returns liveDataDataManagement
    }

    private fun loadDialogInstance() {
        alertDialog = ShadowDialog.getLatestDialog() as AlertDialog
    }

    private fun getLoadingIndicator(): ProgressBar? {
        return alertDialog.findViewById(R.id.importLoadingSpinner)
    }

    private fun getEmptyState(): EmptyStateView? {
        return alertDialog.findViewById(R.id.emptyStateImportDialog)
    }

    private fun getSpinnerContainer(): LinearLayout? {
        return alertDialog.findViewById(R.id.import_spinner_container)
    }

    private fun getSpinner(): Spinner? {
        return alertDialog.findViewById(R.id.spinner_language)
    }

    private fun getInnerWrapper(): LinearLayout? {
        return alertDialog.findViewById(R.id.linear_layout_import)
    }

    private fun getPositiveButton(): Button {
        return alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
    }

    @Test
    fun shouldShowDialog() {
        assertNotNull(alertDialog)
    }

    @Test
    fun shouldShowLoadingIndicator() {
        assertEquals(getLoadingIndicator()!!.visibility, View.VISIBLE)
        assertEquals(getSpinnerContainer()!!.visibility, View.GONE)
        assertFalse(getPositiveButton().isEnabled)
    }

    @Test
    fun shouldShowError() {
        liveDataDataManagement.postValue(listOf())

        assertEquals(getEmptyState()!!.visibility, View.VISIBLE)
        assertEquals(getLoadingIndicator()!!.visibility, View.GONE)
        assertEquals(getSpinnerContainer()!!.visibility, View.GONE)
        assertFalse(getPositiveButton().isEnabled)
    }

    @Test
    fun shouldShowRightLanguagesInSpinner() {
        liveDataDataManagement.postValue(testData)

        val spinner = getSpinner()!!

        assertNull(getEmptyState())
        assertNull(getLoadingIndicator())
        assertEquals(getSpinnerContainer()!!.visibility, View.VISIBLE)

        assertEquals(2, spinner.adapter?.count)
        assertEquals(Language.DE.longString, spinner.adapter?.getItem(0))
        assertEquals(Language.EN.longString, spinner.adapter?.getItem(1))

        assertEquals(Language.DE.longString, spinner.selectedItem)
    }

    @Test
    fun shouldShowTermsAndConditions() {
        liveDataDataManagement.postValue(testData)

        assertFalse(getPositiveButton().isEnabled)

        val innerWrapper = getInnerWrapper()!!
        assertEquals(3, innerWrapper.childCount)
        innerWrapper.getChildAt(0).performClick()

        assertTrue(getPositiveButton().isEnabled)

        innerWrapper.getChildAt(1).performClick()
        innerWrapper.getChildAt(2).performClick()

        getPositiveButton().performClick()

        // Show terms and conditions only once
        val termsAndConditionDialog: AlertDialog = ShadowDialog.getLatestDialog() as AlertDialog
        val textView = termsAndConditionDialog.findViewById<TextView>(android.R.id.message)!!
        val text = textView.text

        assertEquals(Regex(testData[0].copyrightUrl!!).findAll(text).count(), 1)
        assertEquals(Regex(testData[1].copyrightUrl!!).findAll(text).count(), 1)
        assertEquals(Regex(testData[2].copyrightUrl!!).findAll(text).count(), 1)
    }

    @Test
    fun shouldStartDownloadTask() {
        mockkConstructor(ImportVersesTask::class)
        every { anyConstructed<ImportVersesTask>().execute(any()) } just Runs

        liveDataDataManagement.postValue(testData)

        val innerWrapper = getInnerWrapper()!!
        innerWrapper.getChildAt(0).performClick()
        getPositiveButton().performClick()

        // Show terms and conditions only once
        val termsAndConditionDialog: AlertDialog = ShadowDialog.getLatestDialog() as AlertDialog
        termsAndConditionDialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick()

        verify { anyConstructed<ImportVersesTask>().execute(listOf(testData[0])) }
    }
}