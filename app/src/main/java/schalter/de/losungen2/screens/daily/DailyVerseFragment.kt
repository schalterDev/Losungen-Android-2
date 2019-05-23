package schalter.de.losungen2.screens.daily

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import schalter.de.losungen2.R
import schalter.de.losungen2.components.exceptions.TranslatableException
import schalter.de.losungen2.components.verseCard.VerseCardData
import schalter.de.losungen2.dataAccess.daily.DailyVerse
import schalter.de.losungen2.dataAccess.sermon.Sermon
import schalter.de.losungen2.screens.ARG_DATE
import schalter.de.losungen2.screens.VerseListDateFragment
import schalter.de.losungen2.utils.Share
import java.util.*

const val TAG_DEBUG = "Losungen"

/**
 * A simple [Fragment] subclass.
 * Use the [DailyVerseFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class DailyVerseFragment : VerseListDateFragment() {

    private lateinit var mContext: Context
    private lateinit var mApplication: Application
    private lateinit var mViewModel: DailyVerseModel

    @VisibleForTesting
    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    private fun updateDataByDailyVerse(dailyVerse: DailyVerse?) {
        if (dailyVerse != null) {
            this.updateData(VerseCardData.fromDailyVerseTwoCards(mApplication, dailyVerse))
            this.updateFavouriteMenuItem(dailyVerse.isFavourite)
        } else {
            this.updateData(listOf())
        }
    }

    private fun playSermon(sermon: Sermon) {
        // TODO implement
    }

    private fun showError(exception: Throwable) {
        val errorMessage =
                if (exception is TranslatableException) {
                    exception.getStringForUser(mContext)
                } else {
                    exception.message
                }

        exception.printStackTrace()
        Log.w(TAG_DEBUG, errorMessage)

        val snackBar = Snackbar.make(activity!!.findViewById(android.R.id.content),
                errorMessage.toString(), Snackbar.LENGTH_LONG)
        snackBar.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_daily_verse, menu)

        mViewModel.getDailyVerse().value?.let {
            updateFavouriteMenuItem(it.isFavourite)
        }

        this.menu = menu
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share -> {
                mViewModel.getDailyVerse().value?.let {
                    Share.dailyVerse(mContext, it)
                }
                return true
            }
            R.id.action_favourite -> {
                mViewModel.toggleFavourite()
                return true
            }
            R.id.action_sermon -> {
                mViewModel.getDailyVerse().value?.let {
                    mViewModel.loadSermon(mContext).observe(this, androidx.lifecycle.Observer { sermonWrapper ->
                        if (sermonWrapper.error != null) {
                            showError(sermonWrapper.error)
                        } else {
                            playSermon(sermonWrapper.value!!)
                        }
                    })
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        mContext = view!!.context
        mApplication = activity!!.application

        mViewModel = ViewModelProviders.of(this,
                DailyVerseModelFactory(mContext, date!!)).get(DailyVerseModel::class.java)

        mViewModel.getDailyVerse().observe(this, androidx.lifecycle.Observer { dailyVerse ->
            updateDataByDailyVerse(dailyVerse)
        })

        return view
    }

    private fun updateFavouriteMenuItem(isFavourite: Boolean) {
        if (isFavourite) {
            menu?.getItem(1)?.icon = resources
                    .getDrawable(R.drawable.ic_action_favorite)
        } else {
            menu?.getItem(1)?.icon = resources
                    .getDrawable(R.drawable.ic_action_favorite_border)
        }

        activity!!.invalidateOptionsMenu()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param verseDate date of the verse to show
         * @return A new instance of fragment DailyVerseFragment.
         */
        @JvmStatic
        fun newInstance(verseDate: Date) =
                DailyVerseFragment().apply {
                    arguments = Bundle().apply {
                        putLong(ARG_DATE, verseDate.time)
                    }
                }
    }
}
