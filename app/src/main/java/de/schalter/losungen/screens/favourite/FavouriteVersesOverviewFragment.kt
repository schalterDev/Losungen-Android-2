package de.schalter.losungen.screens.favourite

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import de.schalter.losungen.R
import de.schalter.losungen.screens.VerseListFragment

/**
 * A simple [Fragment] subclass.
 */
class FavouriteVersesOverviewFragment : VerseListFragment(showMenuItemToggleNotes = true) {

    private lateinit var mContext: Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        mContext = view!!.context

        emptyStateView.hideButton()
        emptyStateView.setIcon(R.drawable.ic_action_favorite_border)
        emptyStateView.setTitle(R.string.markes_verses_will_be_here)

        updateData(listOf())
        loadData()

        return view
    }

    private fun loadData() {
        val mViewModel = ViewModelProviders.of(this,
                FavouriteVersesModelFactory(activity!!.application, mContext)).get(FavouriteVersesModel::class.java)

        mViewModel.getVerses().observe(this, androidx.lifecycle.Observer { favouriteVerses ->
            updateData(favouriteVerses)
        })
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment
         * @return A new instance of fragment FavouriteVersesOverviewFragment.
         */
        fun newInstance(): FavouriteVersesOverviewFragment {
            return FavouriteVersesOverviewFragment()
        }
    }

}
