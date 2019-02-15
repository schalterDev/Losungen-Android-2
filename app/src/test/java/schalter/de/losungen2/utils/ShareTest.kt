package schalter.de.losungen2.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.test.core.app.ApplicationProvider
import io.mockk.*
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowActivity
import org.robolectric.shadows.ShadowAlertDialog

@RunWith(RobolectricTestRunner::class)
class ShareTest {

    lateinit var context: Context
    lateinit var activity: Activity

    @Before
    fun prepare() {
        activity = Robolectric.buildActivity(FragmentActivity::class.java).create().start().resume().get()
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun shouldShowTextListDialog() {
        val textList: List<TextList> = listOf(
                TextList("titleDialog1", "text1"),
                TextList("titleDialog2", "text2")
        )

        Share.textListDialog(activity, textList)

        val alertDialog = ShadowAlertDialog.getLatestDialog() as AlertDialog
        val listView = alertDialog.listView
        assertEquals(2, listView.count)
        assertEquals(textList[0].titleInDialog, listView.getItemAtPosition(0).toString())
        assertEquals(textList[1].titleInDialog, listView.getItemAtPosition(1).toString())

        mockkObject(Share)
        every { Share.text(any(), any()) } just Runs

        shadowOf(listView).performItemClick(0)

        verify { Share.text(any(), textList[0].text) }
    }

    @Test
    fun shouldShowTextOnly() {
        val text = "Text"

        Share.text(context, text)

        val shadowActivity: ShadowActivity = shadowOf(activity)
        val startedIntent: Intent = shadowActivity.nextStartedActivity
        assertEquals(Intent.ACTION_CHOOSER, startedIntent.action)

        val intentShare = startedIntent.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)

        assertEquals(null, intentShare.getStringExtra(Intent.EXTRA_SUBJECT))
        assertEquals(text, intentShare.getStringExtra(Intent.EXTRA_TEXT))
    }

}