package de.schalter.losungen.components.verseCard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import de.schalter.losungen.R
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class VerseCardViewTest {

    private lateinit var context: Context
    private lateinit var verseCardView: VerseCardView
    private lateinit var title1TextView: TextView
    private lateinit var title2TextView: TextView
    private lateinit var verse1TextView: TextView
    private lateinit var verse2TextView: TextView
    private lateinit var bible1TextView: TextView
    private lateinit var bible2TextView: TextView

    @Before
    fun loadView() {
        context = ApplicationProvider.getApplicationContext()
        context.setTheme(R.style.Theme_Blue)
        verseCardView = LayoutInflater
                .from(context)
                .inflate(R.layout.verse_card, VerseCardView(context), true)
                as VerseCardView

        title1TextView = verseCardView.findViewById(R.id.verseTitle)
        title2TextView = verseCardView.findViewById(R.id.verseTitle2)
        verse1TextView = verseCardView.findViewById(R.id.verseText)
        verse2TextView = verseCardView.findViewById(R.id.verseText2)
        bible1TextView = verseCardView.findViewById(R.id.verseInBible)
        bible2TextView = verseCardView.findViewById(R.id.verseInBible2)
    }

    @Test
    fun shouldShowDataAndHideNotUsedViews() {
        val title = "Title"
        val verse = "Verse"
        val bible = "Bible"

        val title2 = "Title2"
        val verse2 = "Verse2"
        val bible2 = "Bible2"

        verseCardView.setData(VerseCardData(title, verse, bible, type = VerseCardData.Type.DAILY))

        assertThat(title1TextView.text as String, equalTo(title))
        assertThat(verse1TextView.text as String, equalTo(verse))
        assertThat(bible1TextView.text as String, equalTo(bible))

        assertThat(title2TextView.visibility, equalTo(View.GONE))
        assertThat(bible2TextView.visibility, equalTo(View.GONE))
        assertThat(verse2TextView.visibility, equalTo(View.GONE))

        verseCardView.setData(VerseCardData(title, verse, bible, title2, verse2, bible2, type = VerseCardData.Type.DAILY))

        assertThat(title2TextView.visibility, equalTo(View.VISIBLE))
        assertThat(bible2TextView.visibility, equalTo(View.VISIBLE))
        assertThat(verse2TextView.visibility, equalTo(View.VISIBLE))

        assertThat(title2TextView.text as String, equalTo(title2))
        assertThat(verse2TextView.text as String, equalTo(verse2))
        assertThat(bible2TextView.text as String, equalTo(bible2))
    }

    @Test
    fun shouldShowDataWithDataObject() {
        val verseCardData = VerseCardData(
                "title",
                "text",
                "verse",
                "title2",
                "text2",
                "verse2",
                true,
                type = VerseCardData.Type.DAILY
        )

        verseCardView.setData(verseCardData)

        assertThat(title1TextView.text as String, equalTo(verseCardData.title))
        assertThat(verse1TextView.text as String, equalTo(verseCardData.text))
        assertThat(bible1TextView.text as String, equalTo(verseCardData.verse))
        assertThat(title2TextView.text as String, equalTo(verseCardData.title2))
        assertThat(verse2TextView.text as String, equalTo(verseCardData.text2))
        assertThat(bible2TextView.text as String, equalTo(verseCardData.verse2))
    }

}