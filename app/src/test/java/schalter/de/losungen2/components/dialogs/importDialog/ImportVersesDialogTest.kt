package schalter.de.losungen2.components.dialogs.importDialog

import android.app.Application
import android.app.Dialog
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import io.mockk.every
import io.mockk.mockkConstructor
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowDialog
import schalter.de.losungen2.R
import schalter.de.losungen2.components.emptyState.EmptyStateView
import schalter.de.losungen2.utils.CoroutineUtils
import schalter.de.losungen2.utils.TestApplication


@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class ImportVersesDialogTest {

    private lateinit var context: Application
    private lateinit var alertDialog: Dialog

    private var liveDataDataManagement = MutableLiveData<List<DataManagement.YearLanguageUrl>>()

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
        alertDialog = ShadowDialog.getLatestDialog()
    }

    private fun getLoadingIndicator(): ProgressBar? {
        return alertDialog.findViewById(R.id.importLoadingSpinner)
    }

    private fun getEmptyState(): EmptyStateView? {
        return alertDialog.findViewById(R.id.emptyStateImportDialog)
    }

    @Test
    fun shouldShowDialog() {
        assertNotNull(alertDialog)
    }

    @Test
    fun shouldShowLoadingIndicator() {
        assertThat(getLoadingIndicator()!!.visibility, equalTo(View.VISIBLE))
    }

    @Test
    fun shouldShowError() {
        liveDataDataManagement.postValue(listOf())

        assertThat(getEmptyState()!!.visibility, equalTo(View.VISIBLE))
        assertThat(getLoadingIndicator()!!.visibility, equalTo(View.GONE))
    }

    // TODO add tests for successfull data
}