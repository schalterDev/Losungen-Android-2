package schalter.de.losungen2.components.dialogs.importDialog

import android.app.Application
import android.app.Dialog
import android.view.View
import android.widget.ProgressBar
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowAlertDialog
import schalter.de.losungen2.R
import schalter.de.losungen2.components.emptyState.EmptyStateView
import schalter.de.losungen2.utils.TestApplication


@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class ImportVersesDialogTest {

    private lateinit var context: Application
    private lateinit var dialog: ImportVersesDialog
    private lateinit var alertDialog: Dialog

    @Before
    fun loadDialog() {
        context = ApplicationProvider.getApplicationContext()
        dialog = ImportVersesDialog(context)
    }

    private fun loadDialogInstance() {
        dialog.show()
        alertDialog = ShadowAlertDialog.getLatestDialog()
    }

    private fun getLoadingIndicator(): ProgressBar? {
        return alertDialog.findViewById<ProgressBar?>(R.id.importLoadingSpinner)
    }

    private fun getEmptyState(): EmptyStateView? {
        return alertDialog.findViewById<EmptyStateView?>(R.id.emptyStateImportDialog)
    }

    @Test
    fun shouldShowDialog() {
        this.loadDialogInstance()
        assertNotNull(alertDialog)
    }

    @Test
    fun shouldShowLoadingIndicator() {
        this.loadDialogInstance()

        assertThat(getLoadingIndicator()!!.visibility, equalTo(View.VISIBLE))
//        every { mockClient.executeRequest(any()).statusCode } returns 200
//        every { mockClient.executeRequest(any()).responseMessage } returns "OK"
//        every { mockClient.executeRequest(any()).data } returns someJson.toByteArray()
    }

    @Test
    fun shouldShowError() {
        // Mock DataManagement
        this.loadDialogInstance()

//        await().atMost(Duration.TEN_SECONDS).until { getLoadingIndicator()?.visibility == View.GONE }

        Thread.sleep(1000)
        assertThat(getLoadingIndicator()!!.visibility, equalTo(View.GONE))
        assertThat(getEmptyState()!!.visibility, equalTo(View.VISIBLE))
    }
}