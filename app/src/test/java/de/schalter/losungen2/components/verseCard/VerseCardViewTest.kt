package de.schalter.losungen2.components.verseCard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import de.schalter.losungen2.R

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
    fun shouldShowData() {
        val title = "Title"
        val verse = "Verse"
        val bible = "Bible"

        val title2 = "Title2"
        val verse2 = "Verse2"
        val bible2 = "Bible2"

        verseCardView.setTitle(title)
        verseCardView.setVerse(verse)
        verseCardView.setVerseInBible(bible)

        assertThat(title1TextView.text as String, equalTo(title))
        assertThat(verse1TextView.text as String, equalTo(verse))
        assertThat(bible1TextView.text as String, equalTo(bible))

        assertThat(title2TextView.visibility, equalTo(View.GONE))
        assertThat(bible2TextView.visibility, equalTo(View.GONE))
        assertThat(verse2TextView.visibility, equalTo(View.GONE))

        verseCardView.setTitle2(title2)
        verseCardView.setVerse2(verse2)
        verseCardView.setVerseInBible2(bible2)

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
                true
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