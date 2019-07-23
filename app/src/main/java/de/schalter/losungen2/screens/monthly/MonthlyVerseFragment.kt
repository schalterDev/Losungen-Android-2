package de.schalter.losungen2.screens.monthly

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import de.schalter.losungen2.screens.ARG_DATE
import de.schalter.losungen2.screens.VerseListDateFragment
import java.util.*

/**
 * A [Fragment] subclass.
 * Use the [MonthlyVerseFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MonthlyVerseFragment : VerseListDateFragment() {

    private lateinit var mContext: Context
    private lateinit var mApplication: Application

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        mContext = view!!.context
        mApplication = activity!!.application

        this.updateData(listOf())
        date?.let { date ->
            val viewModel = ViewModelProviders.of(this,
                    MonthlyVerseModelFactory(mApplication, mContext, date)).get(MonthlyVerseModel::class.java)

            viewModel.getVerses().observe(this, androidx.lifecycle.Observer { verses ->
                this.updateData(verses)
            })
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param verseDate date of the vers to show
         * @return A new instance of fragment DailyVerseFragment.
         */
        @JvmStatic
        fun newInstance(verseDate: Date) =
                MonthlyVerseFragment().apply {
                    arguments = Bundle().apply {
                        putLong(ARG_DATE, verseDate.time)
                    }
                }
    }
}
