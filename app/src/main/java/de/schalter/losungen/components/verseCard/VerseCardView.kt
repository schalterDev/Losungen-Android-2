package de.schalter.losungen.components.verseCard

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import de.schalter.losungen.R
import de.schalter.losungen.components.dialogs.openVerseExternal.OpenExternalDialog
import de.schalter.losungen.dataAccess.VersesDatabase
import de.schalter.losungen.utils.openExternal.BibleVerse
import de.schalter.losungen.utils.openExternal.BibleVerseParseException
import kotlinx.android.synthetic.main.verse_card.view.*

class VerseCardView : FrameLayout {

    private var titleView: TextView
    private var verseTextView: TextView
    private var verseInBibleView: TextView
    private var titleView2: TextView
    private var verseTextView2: TextView
    private var verseInBibleView2: TextView
    private var imageFavourite: ImageView
    private var notesView: TextView

    private var verseCardViewModel = VerseCardViewModel(VersesDatabase.provideVerseDatabase(context))

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

        notesView = view.findViewById<TextView>(R.id.verseNotes).also {
            it.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    verseCardViewModel.updateNotes(s.toString())
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        hideSecondVerse()

        this.setOnClickListener {
            try {
                // only one verse is shown
                if (verseInBibleView2.visibility == View.GONE) {
                    val bibleVerse = BibleVerse(verseInBible.text as String)
                    OpenExternalDialog(context).open(bibleVerse)
                } else {
                    val items = listOf(
                            context.getString(R.string.old_testament_card_title),
                            context.getString(R.string.new_testament_card_title))

                    val builder = androidx.appcompat.app.AlertDialog.Builder(context)
                    builder.setTitle(R.string.open_verse_external)
                    builder.setCancelable(true)
                    builder.setItems(items.toTypedArray()) { _, which ->
                        val bibleVerse: BibleVerse = if (which == 0) {
                            BibleVerse(verseInBible.text as String)
                        } else {
                            BibleVerse(verseInBible2.text as String)
                        }
                        OpenExternalDialog(context).open(bibleVerse)
                    }
                    builder.show()
                }
            } catch (e: BibleVerseParseException) {
                Toast.makeText(context, R.string.could_not_parse_verse, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setTitle(title: String) {
        titleView.text = title
    }

    private fun setVerse(verse: String) {
        verseTextView.text = verse
    }

    private fun setVerseInBible(verseInBible: String) {
        verseInBibleView.text = verseInBible
    }

    private fun setTitle2(title: String) {
        titleView2.text = title
        titleView2.visibility = View.VISIBLE
    }

    private fun setVerse2(verse: String) {
        verseTextView2.text = verse
        verseTextView2.visibility = View.VISIBLE
    }

    private fun setVerseInBible2(verseInBible: String) {
        verseInBibleView2.text = verseInBible
        verseInBibleView2.visibility = View.VISIBLE
    }

    private fun setIsFavourite(isFavourite: Boolean) {
        val imageResource = if (isFavourite) {
            R.drawable.ic_action_favorite
        } else {
            R.drawable.ic_action_favorite_border
        }

        imageFavourite.setImageResource(imageResource)
    }

    fun setData(verseCardData: VerseCardData) {
        verseCardViewModel.setData(verseCardData)

        setVisibilityFavouriteIcon(verseCardData.showFavouriteIcon)
        imageFavourite.setOnClickListener {
            verseCardViewModel.toggleIsFavourite()
        }

        setTitle(verseCardData.title)
        setVerse(verseCardData.text)
        setVerseInBible(verseCardData.verse)
        setIsFavourite(verseCardData.isFavourite)

        verseCardData.title2?.let { setTitle2(it) } ?: run {
            titleView2.visibility = View.GONE
        }
        verseCardData.text2?.let { setVerse2(it) } ?: run {
            verseTextView2.visibility = View.GONE
        }
        verseCardData.verse2?.let { setVerseInBible2(it) } ?: run {
            verseInBibleView2.visibility = View.GONE
        }
        verseCardData.notes?.let {
            notesView.text = it
        }
    }

    private fun setVisibilityFavouriteIcon(visible: Boolean) {
        if (visible) {
            imageFavourite.visibility = View.VISIBLE
        } else {
            imageFavourite.visibility = View.GONE
        }
    }

    fun getData(): VerseCardData {
        return verseCardViewModel.getData()!!
    }

    private fun hideSecondVerse() {
        titleView2.visibility = View.GONE
        verseTextView2.visibility = View.GONE
        verseInBibleView2.visibility = View.GONE
    }

    fun showNotes(showNotes: Boolean) {
        if (showNotes) {
            notesView.visibility = View.VISIBLE
        } else {
            notesView.visibility = View.GONE
        }
    }

    fun saveNotes() {
        verseCardViewModel.saveNotes()
    }
}