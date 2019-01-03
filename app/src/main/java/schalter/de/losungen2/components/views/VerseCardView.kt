package schalter.de.losungen2.components.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import schalter.de.losungen2.R

class VerseCardView : FrameLayout {

    private var titleView: TextView
    private var verseTextView: TextView
    private var verseInBibleView: TextView
    private var titleView2: TextView
    private var verseTextView2: TextView
    private var verseInBibleView2: TextView

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle)

    init {
        val view = View.inflate(context, R.layout.verse_card, null)
        addView(view)
        titleView = view.findViewById(R.id.verseTitle)
        verseTextView = view.findViewById(R.id.verseText)
        verseInBibleView = view.findViewById(R.id.verseInBible)

        titleView2 = view.findViewById(R.id.verseTitle2)
        verseTextView2 = view.findViewById(R.id.verseText2)
        verseInBibleView2 = view.findViewById(R.id.verseInBible2)

        hideSecondVerse()
    }

    fun setTitle(titleRes: Int) {
        titleView.text = context.getString(titleRes)
    }

    fun setVerse(verse: String) {
        verseTextView.text = verse
    }

    fun setVerseInBible(verseInBible: String) {
        verseInBibleView.text = verseInBible
    }

    private fun hideSecondVerse() {
        titleView2.visibility = View.GONE
        verseTextView2.visibility = View.GONE
        verseInBibleView2.visibility = View.GONE
    }

    data class VerseCardData(
            var title: String,
            var verse: String,
            var verseInBible: String,
            var title2: String,
            var verse2: String,
            var verseInBible2: String
    )
}