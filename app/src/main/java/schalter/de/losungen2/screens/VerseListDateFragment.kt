package schalter.de.losungen2.screens

import android.os.Bundle
import java.util.*

const val ARG_VERSE_DATE = "verse_date"

abstract class VerseListDateFragment : VerseListFragment() {

    protected var date: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            date = Date(it.getLong(ARG_VERSE_DATE))
        }
    }

}