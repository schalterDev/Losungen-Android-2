package schalter.de.losungen2.components.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import schalter.de.losungen2.R

class OneVerseCardView : FrameLayout {

    private var titleView: TextView
    private var verseTextView: TextView
    private var verseInBibleView: TextView

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle)

    init {
        val view = View.inflate(context, R.layout.one_verse_card, null)
        addView(view)
        titleView = view.findViewById(R.id.oneVerseTitle)
        verseTextView = view.findViewById(R.id.oneVerseText)
        verseInBibleView = view.findViewById(R.id.oneVerseInBible)
    }

    fun setTitle(titleRes: Int) { titleView.text = context.getString(titleRes) }
    fun setVerse(verse: String) { verseTextView.text = verse }
    fun setVerseInBible(verseInBible: String) { verseInBibleView.text = verseInBible }
}