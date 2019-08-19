package de.schalter.losungen.components.dialogs.widgetStyleChooser

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import de.schalter.losungen.R

class WidgetStyleChooserDialogGridAdapter(
        private val themes: List<WidgetStyle>,
        private val textToShow: String) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder = if (convertView == null) {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.dialog_widget_picker_grid_item, parent, false)
            ViewHolder(view)
        } else {
            convertView.tag as ViewHolder
        }

        val theme = themes[position]

        viewHolder.container.setBackgroundColor(theme.backgroundColor)
        viewHolder.textView.text = textToShow
        viewHolder.textView.setTextColor(theme.fontColor)
        viewHolder.textView.textSize = theme.fontSize

        return viewHolder.itemView
    }

    override fun getItem(position: Int): WidgetStyle = themes[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = themes.size

    private class ViewHolder(val itemView: View) {

        val container: RelativeLayout
        val textView: TextView

        init {
            itemView.tag = this
            container = itemView.findViewById(R.id.relLayout_widget)
            textView = itemView.findViewById(R.id.textView_widget)
        }
    }
}