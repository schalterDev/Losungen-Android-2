package de.schalter.losungen.screens.widgetsOverview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.schalter.losungen.R
import de.schalter.losungen.components.emptyState.EmptyStateView
import de.schalter.losungen.firebase.AnalyticsFragment
import de.schalter.losungen.widgets.WidgetData


class WidgetsOverviewFragment : AnalyticsFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateView: EmptyStateView

    companion object {
        fun newInstance() = WidgetsOverviewFragment()
    }

    private lateinit var viewModel: WidgetsOverviewViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_widget_overview, container, false).apply {
            recyclerView = this.findViewById(R.id.recycler_widgets)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            emptyStateView = this.findViewById(R.id.emptyState)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(
                this,
                WidgetsOverviewViewModelFactory(activity!!.application, requireContext())
        ).get(WidgetsOverviewViewModel::class.java)

        viewModel.getAllWidgets().observe(this, Observer { updateViews(it) })
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadWidgetData()
    }

    private fun updateViews(data: List<WidgetData>) {
        if (data.size > 0) {
            recyclerView.visibility = View.VISIBLE
            emptyStateView.visibility = View.GONE

            val adapter = WidgetsRecyclerViewAdapter(requireActivity(), data)
            recyclerView.adapter = adapter
        } else {
            recyclerView.visibility = View.GONE
            emptyStateView.visibility = View.VISIBLE
        }
    }

}
