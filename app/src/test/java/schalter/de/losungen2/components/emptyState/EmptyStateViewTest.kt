package schalter.de.losungen2.components.emptyState

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowDialog
import schalter.de.losungen2.R
import schalter.de.losungen2.utils.TestApplication

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class EmptyStateViewTest {

    private lateinit var context: Context
    private lateinit var emptyStateView: EmptyStateView

    @Before
    fun loadView() {
        context = getApplicationContext()
        emptyStateView = LayoutInflater
                .from(context)
                .inflate(R.layout.empty_state, EmptyStateView(context), true)
                as EmptyStateView
    }

    @Test
    fun buttonShouldShowAndInteract() {
        val button: Button = emptyStateView.findViewById(R.id.emptyStateImport)
        assertThat(button.text as String, equalTo(context.getString(R.string.import_new_data)))

        button.performClick()

        val dialog = ShadowDialog.getLatestDialog()
        assertNotNull(dialog)
        val dialogView = dialog.findViewById<View>(R.id.import_dialog_layout)
        assertNotNull(dialogView)
    }

}