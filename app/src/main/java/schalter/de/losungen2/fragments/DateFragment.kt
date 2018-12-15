package schalter.de.losungen2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import java.util.*

private const val ARG_DATE = "arg_date"

open class DateFragment: Fragment() {
    var date: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            date = Date(it.getLong(ARG_DATE))
        }
    }
}