package schalter.de.losungen2.components.dialogs.importDialog

import android.app.Application
import android.app.Dialog
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragment
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowAlertDialog
import schalter.de.losungen2.R
import schalter.de.losungen2.utils.TestApplication


@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class ImportVersesDialogTest {

    private lateinit var context: Application
    private lateinit var alertDialog: Dialog
    private lateinit var fragmentScenario: FragmentScenario<ImportVersesDialog>

    @Before
    fun loadDialog() {
        context = ApplicationProvider.getApplicationContext()

//        val fragment = ImportVersesDialog()
//        val activity: FragmentActivity = Robolectric.buildActivity(FragmentActivity::class.java).create().start().resume().get()
//        val fragmentManager = activity.supportFragmentManager
//        val fragmentTransaction = fragmentManager.beginTransaction()
//        fragment.show(fragmentManager, "importDialog")
//        fragmentTransaction.commit()
//        fragmentManager.executePendingTransactions()

        fragmentScenario = launchFragment<ImportVersesDialog>()

        this.loadDialogInstance()
    }

    private fun loadDialogInstance() {
        alertDialog = ShadowAlertDialog.getLatestDialog()
    }

    private fun getLoadingIndicator(): ViewInteraction {
        return onView(withId(R.id.importLoadingSpinner)).inRoot(isDialog())
    }

    private fun getEmptyState(): ViewInteraction {
        return onView(withId(R.id.emptyStateImportDialog))
    }

    @Test
    fun shouldShowDialog() {
        assertNotNull(alertDialog)
    }

    // TODO how to test dialog? Did not find a good, working way

//    @Test
//    fun shouldShowLoadingIndicator() {
//        getLoadingIndicator().check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun shouldShowError() {
////        await().atMost(Duration.TEN_SECONDS).until { getLoadingIndicator() == null || getLoadingIndicator()?.visibility == View.GONE }
//
////        assertThat(getLoadingIndicator()!!.visibility, equalTo(View.GONE))
////        assertThat(getEmptyState()!!.visibility, equalTo(View.VISIBLE))
//    }
}