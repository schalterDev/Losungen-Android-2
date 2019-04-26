package schalter.de.losungen2.components.emptyState

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import schalter.de.losungen2.R
import schalter.de.losungen2.utils.TestApplication


@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class EmptyStateViewTest {

    private lateinit var context: Context
    private lateinit var emptyStateView: EmptyStateView

    private val title = R.string.monthly_verse_title
    private val icon = R.drawable.ic_cloud_off
    private val buttonText = R.string.import_new_data

    @Before
    fun loadView() {
        context = getApplicationContext()

        Robolectric.buildAttributeSet()

        emptyStateView = LayoutInflater
                .from(context)
                .inflate(R.layout.empty_state, EmptyStateView(context), true)
                as EmptyStateView

        emptyStateView.setButtonText(buttonText)
        emptyStateView.setIcon(icon)
        emptyStateView.setTitle(title)
    }

    @Test
    fun buttonShouldShowAndInteract() {
        val button: Button = emptyStateView.findViewById(R.id.emptyStateImport)
        assertThat(button.text as String, equalTo(context.getString(buttonText)))

        var buttonClicked = false
        emptyStateView.onButtonClick { buttonClicked = true }
        button.performClick()

        assertTrue(buttonClicked)
    }

    @Test
    fun showTitle() {
        val titleElement = emptyStateView.findViewById<TextView>(R.id.emptyStateTitle)
        assertThat(titleElement.text as String, equalTo(context.getString(title)))
    }

    @Test
    fun showIcon() {
        val imageView = emptyStateView.findViewById<ImageView>(schalter.de.losungen2.R.id.emptyStateIcon)
        val shadowDrawable = Shadows.shadowOf(imageView.drawable)
        assertEquals(icon, shadowDrawable.createdFromResId)
    }
}