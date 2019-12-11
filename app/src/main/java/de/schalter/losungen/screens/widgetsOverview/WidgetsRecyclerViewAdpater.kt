package de.schalter.losungen.screens.widgetsOverview

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import de.schalter.losungen.R
import de.schalter.losungen.components.widgetVerse.WidgetPreview
import de.schalter.losungen.widgets.AppWidgetActivity
import de.schalter.losungen.widgets.WidgetData


class WidgetsRecyclerViewAdapter(private val activity: Activity, private val widgets: List<WidgetData>) : RecyclerView.Adapter<WidgetsRecyclerViewAdapter.ViewHolder>() {

    var clickListener: AdapterView.OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = WidgetPreview(activity)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = widgets.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
            holder.updateData(widgets[position])

    inner class ViewHolder(private val widgetPreview: WidgetPreview) : RecyclerView.ViewHolder(widgetPreview), View.OnClickListener {

        private var widgetData: WidgetData? = null

        init {
            widgetPreview.findViewById<View>(R.id.widget_preview_container).apply {
                this.setOnClickListener(this@ViewHolder)
            }
        }

        fun updateData(widgetData: WidgetData) {
            this.widgetData = widgetData
            widgetPreview.setWidgetData(widgetData)
        }

        override fun onClick(v: View?) {
            clickListener?.onItemClick(null, v, adapterPosition, adapterPosition.toLong()) ?: run {
                widgetData?.let { widgetData ->
                    val intent = Intent(activity, AppWidgetActivity::class.java)
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetData.widgetId)
                    intent.putExtra(WidgetData.EXTRA_EDIT_WIDGET, true)
                    activity.startActivity(intent)
                }
            }
        }

    }

}