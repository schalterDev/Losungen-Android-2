package de.schalter.customize

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ChooseStylePreferenceDialogGridAdapter(private val themes: List<CustomizeTheme>, private val titleToolbar: String) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder = if (convertView == null) {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.theme_picker_item, parent, false)
            ViewHolder(view, titleToolbar)
        } else {
            convertView.tag as ViewHolder
        }

        val theme = themes[position]

        viewHolder.toolbar.setBackgroundColor(theme.primary)
        viewHolder.toolbarText.setTextColor(theme.toolbarIcon)
        viewHolder.body.setBackgroundColor(theme.windowBackground)
        viewHolder.fab.apply {
            this.backgroundTintList = ColorStateList.valueOf(theme.accent)
            this.rippleColor = theme.toolbarIcon
        }

        return viewHolder.itemView
    }

    override fun getItem(position: Int): CustomizeTheme = themes[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = themes.size

    private class ViewHolder(val itemView: View, val titleToolbar: String) {

        val toolbar: LinearLayout
        val toolbarText: TextView
        val body: LinearLayout
        val fab: FloatingActionButton

        init {
            itemView.tag = this

            toolbar = itemView.findViewById(R.id.item_toolbar)
            toolbarText = itemView.findViewById(R.id.item_title)
            body = itemView.findViewById(R.id.item_body)
            fab = itemView.findViewById(R.id.item_fab)


            toolbarText.text = titleToolbar
        }
    }
}