package schalter.de.losungen2

import android.view.View
import android.widget.TextView

class VerseCardTestUtils {
    data class VerseCardData(
            var title: String,
            var text: String,
            var verse: String,
            var title2: String? = null,
            var text2: String? = null,
            var verse2: String? = null)

    companion object {
        fun getDataFromVerseCard(viewOfCard: View): VerseCardData {
            val title = viewOfCard.findViewById<TextView>(R.id.verseTitle)?.text as String?
            val text = viewOfCard.findViewById<TextView>(R.id.verseText)?.text as String?
            val verse = viewOfCard.findViewById<TextView>(R.id.verseInBible)?.text as String?

            val title2 = viewOfCard.findViewById<TextView>(R.id.verseTitle2)?.text as String?
            val text2 = viewOfCard.findViewById<TextView>(R.id.verseText2)?.text as String?
            val verse2 = viewOfCard.findViewById<TextView>(R.id.verseInBible2)?.text as String?

            return VerseCardData(title!!, text!!, verse!!, title2, text2, verse2)
        }

        fun isFirstVerseVisible(viewOfCard: View): Boolean =
                viewOfCard.findViewById<TextView>(R.id.verseTitle)?.visibility == View.VISIBLE &&
                        viewOfCard.findViewById<TextView>(R.id.verseText)?.visibility == View.VISIBLE &&
                        viewOfCard.findViewById<TextView>(R.id.verseInBible)?.visibility == View.VISIBLE

        fun isSecondVerseVisible(viewOfCard: View): Boolean =
                viewOfCard.findViewById<TextView>(R.id.verseTitle2)?.visibility == View.VISIBLE &&
                        viewOfCard.findViewById<TextView>(R.id.verseText2)?.visibility == View.VISIBLE &&
                        viewOfCard.findViewById<TextView>(R.id.verseInBible2)?.visibility == View.VISIBLE

    }
}