package schalter.de.losungen2.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import schalter.de.losungen2.R
import schalter.de.losungen2.components.dialogs.importDialog.ImportVersesDialog
import schalter.de.losungen2.components.emptyState.EmptyStateView
import schalter.de.losungen2.components.verseCard.VerseCardData
import schalter.de.losungen2.components.verseCard.VerseCardGridAdapter

abstract class VerseListFragment : Fragment() {

    protected lateinit var emptyStateView: EmptyStateView
    private lateinit var recyclerView: RecyclerView
    private lateinit var gridAdapter: VerseCardGridAdapter
    protected lateinit var linearLayout: LinearLayout
    protected lateinit var textViewNotes: TextView
    protected lateinit var buttonShowNotes: Button

    private var importDialog: ImportVersesDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_verse_list, container, false)
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

        textViewNotes = view.findViewById(R.id.text_notes)
        buttonShowNotes = view.findViewById<Button>(R.id.button_add_notes).apply {
            this.setOnClickListener {
                textViewNotes.visibility = View.VISIBLE
                this.visibility = View.GONE
            }
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