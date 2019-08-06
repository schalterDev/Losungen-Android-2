package de.schalter.losungen.screens

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import android.widget.LinearLayout
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import de.schalter.losungen.R
import de.schalter.losungen.components.dialogs.importDialog.ImportVersesDialog
import de.schalter.losungen.components.emptyState.EmptyStateView
import de.schalter.losungen.components.verseCard.VerseCardData
import de.schalter.losungen.components.verseCard.VerseCardGridAdapter
import de.schalter.losungen.firebase.AnalyticsFragment
import de.schalter.losungen.utils.PreferenceTags

abstract class VerseListFragment(
        val layout: Int = R.layout.fragment_verse_list,
        private val showMenuItemToggleNotes: Boolean
) : AnalyticsFragment() {

    protected lateinit var emptyStateView: EmptyStateView
    private lateinit var recyclerView: RecyclerView
    private lateinit var gridAdapter: VerseCardGridAdapter
    protected lateinit var linearLayout: LinearLayout

    @VisibleForTesting
    private var menu: Menu? = null

    private var importDialog: ImportVersesDialog? = null
    private var showNotes: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(showMenuItemToggleNotes)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layout, container, false)
        emptyStateView = view.findViewById(R.id.emptyState)
        recyclerView = view.findViewById(R.id.versesList)
        linearLayout = view.findViewById(R.id.versesLinearLayout)

        recyclerView.layoutManager = VerseCardGridAdapter.getLayoutManager(view.context)
        gridAdapter = VerseCardGridAdapter()
        recyclerView.adapter = gridAdapter

        emptyStateView.onButtonClick {
            importDialog = ImportVersesDialog()
            importDialog!!.show(activity!!.supportFragmentManager, null)
        }

        if (showMenuItemToggleNotes) {
            showNotes(
                    PreferenceManager.getDefaultSharedPreferences(view.context)
                            .getBoolean(PreferenceTags.SHOW_NOTES, false))
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (showMenuItemToggleNotes) {
            inflater.inflate(R.menu.menu_verse_list, menu)

            if (showNotes) {
                menu.getItem(0)?.icon = ContextCompat.getDrawable(context!!, R.drawable.ic_speaker_notes_off)
            } else {
                menu.getItem(0)?.icon = ContextCompat.getDrawable(context!!, R.drawable.ic_speaker_notes)
            }
        }

        this.menu = menu

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_toggle_notes -> {
                showNotes(!showNotes)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun updateShowNotesIcon() {
        activity!!.invalidateOptionsMenu()
    }

    override fun onPause() {
        super.onPause()
        gridAdapter.saveNotes()
    }

    private fun showNotes(showNotes: Boolean) {
        this.showNotes = showNotes
        updateShowNotesIcon()
        gridAdapter.showNotes(showNotes)
    }

    protected fun updateData(list: List<VerseCardData>) {
        if (list.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyStateView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyStateView.visibility = View.GONE
        }

        gridAdapter.setData(list)
    }

}