package de.schalter.losungen.screens.widgetsOverview

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.schalter.losungen.R
import de.schalter.losungen.utils.Wallpaper
import de.schalter.losungen.widgets.AppWidgetActivity
import de.schalter.losungen.widgets.WidgetData


class WidgetsRecyclerViewAdapter(private val activity: Activity, private val widgets: List<WidgetData>) : RecyclerView.Adapter<WidgetsRecyclerViewAdapter.ViewHolder>() {

    var clickListener: AdapterView.OnItemClickListener? = null
    private var mInflater: LayoutInflater = LayoutInflater.from(activity)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = mInflater.inflate(R.layout.app_widget_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = widgets.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
            holder.updateData(widgets[position])

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private var container: LinearLayout
        private var textViewText: TextView
        private var textViewVerse: TextView
        private var widgetData: WidgetData? = null

        init {
            itemView.setOnClickListener(this)
            container = itemView.findViewById(R.id.linearLayout_widgetRow)
            textViewText = itemView.findViewById(R.id.textView_widget)
            textViewVerse = itemView.findViewById(R.id.textViewVerse_widget)
        }

        fun updateData(widgetData: WidgetData) {
            this.widgetData = widgetData

            textViewText.text = widgetData.content.getOrNull(0)?.verseText
            textViewVerse.text = widgetData.content.getOrNull(0)?.verseVerse
            textViewText.textSize = widgetData.fontSize.toFloat()
            textViewVerse.textSize = widgetData.fontSize.toFloat() - 2
            textViewText.setTextColor(widgetData.color)
            textViewVerse.setTextColor(widgetData.color)

            textViewText.setBackgroundColor(widgetData.background)
            textViewVerse.setBackgroundColor(widgetData.background)

            container.background = Wallpaper.getWallpaperDrawable(activity)
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