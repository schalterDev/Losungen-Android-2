package schalter.de.losungen2.screens

import android.os.Bundle
import java.util.*

const val ARG_DATE = "arg_date"

abstract class VerseListDateFragment : VerseListFragment() {

    protected var date: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            date = Date(it.getLong(ARG_DATE))
        }
    }

}