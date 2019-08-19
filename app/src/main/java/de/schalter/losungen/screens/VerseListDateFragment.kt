package de.schalter.losungen.screens

import android.os.Bundle
import de.schalter.losungen.R
import java.util.*

const val ARG_DATE = "arg_date"

abstract class VerseListDateFragment(
        layout: Int = R.layout.fragment_verse_list,
        showMenuItemToggleNotes: Boolean = false
) : VerseListFragment(layout, showMenuItemToggleNotes) {

    protected var date: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            date = Date(it.getLong(ARG_DATE))
        }
    }

}