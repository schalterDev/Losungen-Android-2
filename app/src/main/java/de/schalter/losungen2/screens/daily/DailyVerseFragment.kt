package de.schalter.losungen2.screens.daily

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import de.schalter.losungen2.R
import de.schalter.losungen2.components.exceptions.TranslatableException
import de.schalter.losungen2.components.verseCard.VerseCardData
import de.schalter.losungen2.dataAccess.daily.DailyVerse
import de.schalter.losungen2.dataAccess.sermon.Sermon
import de.schalter.losungen2.screens.ARG_DATE
import de.schalter.losungen2.screens.VerseListDateFragment
import de.schalter.losungen2.sermon.mediaPlayer.MediaPlayerUi
import de.schalter.losungen2.utils.Share
import java.util.*


const val TAG_DEBUG = "Losungen"

/**
 * A simple [Fragment] subclass.
 * Use the [DailyVerseFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class DailyVerseFragment : VerseListDateFragment(R.layout.fragment_verse_list_notes) {

    private lateinit var mContext: Context
    private lateinit var mApplication: Application
    private lateinit var mViewModel: DailyVerseModel
    private var firstData = true

    private lateinit var mediaPlayerUi: MediaPlayerUi

    private lateinit var textViewNotes: TextView
    private lateinit var buttonShowNotes: Button

    @VisibleForTesting
    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPause() {
        super.onPause()
        mediaPlayerUi.unbindService()
    }

    override fun onResume() {
        super.onResume()
        mViewModel.getDailyVerse().value?.date?.let {
            mediaPlayerUi.checkServiceIsRunningAndBind(it.time.toString())
        }
    }

    private fun updateDataByDailyVerse(dailyVerse: DailyVerse?) {
        if (dailyVerse != null) {
            if (firstData) {
                firstData = false
                mediaPlayerUi.checkServiceIsRunningAndBind(dailyVerse.date.time.toString())
                textViewNotes.text = dailyVerse.notes
            }

            if (dailyVerse.notes != null && dailyVerse.notes != "") {
                textViewNotes.visibility = View.VISIBLE
                buttonShowNotes.visibility = View.GONE
            }

            this.updateData(VerseCardData.fromDailyVerseTwoCards(mApplication, dailyVerse))
            this.updateFavouriteMenuItem(dailyVerse.isFavourite)
        } else {
            this.updateData(listOf())
        }
    }

    private fun playSermon(sermon: Sermon) {
        mediaPlayerUi.playAudio(sermon.pathSaved, mViewModel.getDailyVerse().value?.date?.time.toString(), sermon.provider
                ?: mContext.getString(R.string.playing_sermon))
        mediaPlayerUi.setTitle(sermon.provider)
    }

    private fun showError(exception: Throwable) {
        val errorMessage =
                if (exception is TranslatableException) {
                    exception.getStringForUser(mContext)
                } else {
                    exception.message ?: ""
                }

        exception.printStackTrace()
        Log.w(TAG_DEBUG, errorMessage)

        val snackBar = Snackbar.make(activity!!.findViewById(android.R.id.content),
                errorMessage, Snackbar.LENGTH_LONG)
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
            R.id.action_share_sermon -> {
                mViewModel.getDailyVerse().value?.let {
                    Share.sermon(mContext, it.date)
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

        mediaPlayerUi = MediaPlayerUi(mContext).apply {
            this.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        this.linearLayout.addView(mediaPlayerUi)

        buttonShowNotes = view.findViewById<Button>(R.id.button_add_notes).apply {
            this.visibility = View.VISIBLE
            this.setOnClickListener {
                textViewNotes.visibility = View.VISIBLE
                this.visibility = View.GONE
            }
        }

        textViewNotes = view.findViewById<TextView>(R.id.text_notes).apply {
            this.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    mViewModel.saveNotes(s.toString())
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        return view
    }

    private fun updateFavouriteMenuItem(isFavourite: Boolean) {
        if (isFavourite) {
            menu?.getItem(1)?.icon = ContextCompat.getDrawable(mContext, R.drawable.ic_action_favorite)
        } else {
            menu?.getItem(1)?.icon = ContextCompat.getDrawable(mContext, R.drawable.ic_action_favorite_border)
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