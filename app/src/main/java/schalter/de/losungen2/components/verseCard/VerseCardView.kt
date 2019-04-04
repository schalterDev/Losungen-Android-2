package schalter.de.losungen2.components.verseCard

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import schalter.de.losungen2.R

class VerseCardView : FrameLayout {

    private var titleView: TextView
    private var verseTextView: TextView
    private var verseInBibleView: TextView
    private var titleView2: TextView
    private var verseTextView2: TextView
    private var verseInBibleView2: TextView
    private var imageFavourite: ImageView

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

        imageFavourite = view.findViewById(R.id.verseCardFavoriteImage)

        hideSecondVerse()
    }

    fun setTitle(titleRes: Int) {
        titleView.text = context.getString(titleRes)
    }

    fun setTitle(title: String) {
        titleView.text = title
    }

    fun setVerse(verse: String) {
        verseTextView.text = verse
    }

    fun setVerseInBible(verseInBible: String) {
        verseInBibleView.text = verseInBible
    }

    fun setTitle2(titleRes: Int) {
        titleView2.text = context.getString(titleRes)
        titleView2.visibility = View.VISIBLE
    }

    fun setTitle2(title: String) {
        titleView2.text = title;
        titleView2.visibility = View.VISIBLE
    }

    fun setVerse2(verse: String) {
        verseTextView2.text = verse
        verseTextView2.visibility = View.VISIBLE
    }

    fun setVerseInBible2(verseInBible: String) {
        verseInBibleView2.text = verseInBible
        verseInBibleView2.visibility = View.VISIBLE
    }

    fun setIsFavourite(isFavourite: Boolean) {
        val imageResource = if (isFavourite) {
            R.drawable.ic_action_favorite
        } else {
            R.drawable.ic_action_favorite_border
        }

        imageFavourite.setImageResource(imageResource)
    }

    fun setData(verseCardData: VerseCardData) {
        setVisibilityFavouriteIcon(verseCardData.showFavouriteIcon)
        imageFavourite.setOnClickListener { verseCardData.updateIsFavourite?.invoke(!verseCardData.isFavourite) }

        setTitle(verseCardData.title)
        setVerse(verseCardData.text)
        setVerseInBible(verseCardData.verse)
        setIsFavourite(verseCardData.isFavourite)

        verseCardData.title2?.let { setTitle2(it) }
        verseCardData.text2?.let { setVerse2(it) }
        verseCardData.verse2?.let { setVerseInBible2(it) }
    }

    fun setVisibilityFavouriteIcon(visible: Boolean) {
        if (visible) {
            imageFavourite.visibility = View.VISIBLE
        } else {
            imageFavourite.visibility = View.GONE
        }
    }

    fun getData(): VerseCardData {
        return VerseCardData(
                titleView.text as String,
                verseTextView.text as String,
                verseInBibleView.text as String,
                titleView2.text as String,
                verseTextView2.text as String,
                verseInBibleView2.text as String
        )
    }

    private fun hideSecondVerse() {
        titleView2.visibility = View.GONE
        verseTextView2.visibility = View.GONE
        verseInBibleView2.visibility = View.GONE
    }
}