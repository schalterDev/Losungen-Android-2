package de.schalter.losungen.screens.widgetsOverview

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.schalter.losungen.R
import de.schalter.losungen.widgets.AppWidgetActivity
import de.schalter.losungen.widgets.WidgetData


class WidgetsRecyclerViewAdapter(private val activity: Activity, private val widgets: List<WidgetData>) : RecyclerView.Adapter<WidgetsRecyclerViewAdapter.ViewHolder>() {

    private var mInflater: LayoutInflater = LayoutInflater.from(activity)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = mInflater.inflate(R.layout.app_widget, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = widgets.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
            holder.updateData(widgets[position])

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private var textView: TextView
        private var widgetData: WidgetData? = null

        init {
            itemView.setOnClickListener(this)
            textView = itemView.findViewById(R.id.textView_widget)
            itemView.findViewById<RelativeLayout>(R.id.relLayout_widget).apply {
                val params: ViewGroup.LayoutParams = this.layoutParams
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                this.layoutParams = params
            }
        }

        fun updateData(widgetData: WidgetData) {
            this.widgetData = widgetData

            textView.text = widgetData.contentToShow
            textView.textSize = widgetData.fontSize.toFloat()
            textView.text = widgetData.contentToShow
            textView.setTextColor(widgetData.color)
            textView.setBackgroundColor(widgetData.background)
        }

        override fun onClick(v: View?) {
            widgetData?.let { widgetData ->
                val intent = Intent(activity, AppWidgetActivity::class.java)
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetData.widgetId)
                intent.putExtra(WidgetData.EXTRA_EDIT_WIDGET, true)
                activity.startActivity(intent)
            }
        }

    }

}