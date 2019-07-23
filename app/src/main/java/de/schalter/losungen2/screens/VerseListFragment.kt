package de.schalter.losungen2.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import de.schalter.losungen2.R
import de.schalter.losungen2.components.dialogs.importDialog.ImportVersesDialog
import de.schalter.losungen2.components.emptyState.EmptyStateView
import de.schalter.losungen2.components.verseCard.VerseCardData
import de.schalter.losungen2.components.verseCard.VerseCardGridAdapter
import de.schalter.losungen2.firebase.AnalyticsFragment

abstract class VerseListFragment(val layout: Int = R.layout.fragment_verse_list) : AnalyticsFragment() {

    protected lateinit var emptyStateView: EmptyStateView
    private lateinit var recyclerView: RecyclerView
    private lateinit var gridAdapter: VerseCardGridAdapter
    protected lateinit var linearLayout: LinearLayout
    protected lateinit var actionBar: ActionBar

    private var importDialog: ImportVersesDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layout, container, false)
        emptyStateView = view.findViewById(R.id.emptyState)
        recyclerView = view.findViewById(R.id.versesList)
        linearLayout = view.findViewById(R.id.versesLinearLayout)
        actionBar = (activity as AppCompatActivity).supportActionBar!!

        recyclerView.layoutManager = VerseCardGridAdapter.getLayoutManager(view.context)
        gridAdapter = VerseCardGridAdapter()
        recyclerView.adapter = gridAdapter

        emptyStateView.onButtonClick {
            importDialog = ImportVersesDialog()
            importDialog!!.show(activity!!.supportFragmentManager, null)
        }

        return view
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