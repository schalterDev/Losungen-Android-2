package de.schalter.losungen.screens.search

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import de.schalter.losungen.R
import de.schalter.losungen.screens.VerseListFragment

/**
 * A simple [Fragment] subclass.
 */
class SearchFragment : VerseListFragment(R.layout.fragment_verse_list_search) {

    private lateinit var mContext: Context
    private lateinit var mViewModel: SearchModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        mContext = view!!.context

        emptyStateView.apply {
            this.hideButton()
            this.setIcon(R.drawable.ic_search)
            this.setTitle(R.string.search_verses)
        }

        updateData(listOf())

        mViewModel = ViewModelProviders.of(this,
                SearchModelFactory(activity!!.application, mContext)).get(SearchModel::class.java)

        return view
    }

    private fun setupSearchView(searchView: SearchView) {
        searchView.setIconifiedByDefault(false)
        searchView.isIconified = false
        searchView.requestFocusFromTouch()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                mViewModel.search(query).observe(this@SearchFragment, Observer {
                    updateData(it)
                })
                return true
            }

            override fun onQueryTextChange(newText: String?) = true
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search_verses, menu)

        menu.findItem(R.id.action_search).apply {
            val searchView = this.actionView as SearchView
            setupSearchView(searchView)
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                // TODO maybe implement
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment
         * @return A new instance of fragment SearchFragment.
         */
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }

}
