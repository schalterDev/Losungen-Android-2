package schalter.de.losungen2

import android.view.View
import android.widget.TextView

class UnitTestUtils {

    data class OneVerseCardData(var title: String, var text: String, var verse: String)

    companion object {
        fun getDataFromOneVerseCard(viewOfCard: View): OneVerseCardData {
            val title = viewOfCard.findViewById<TextView>(R.id.oneVerseTitle)?.text as String?
            val text = viewOfCard.findViewById<TextView>(R.id.oneVerseText)?.text as String?
            val verse = viewOfCard.findViewById<TextView>(R.id.oneVerseInBible)?.text as String?

            return OneVerseCardData(title!!, text!!, verse!!)
        }
    }
}