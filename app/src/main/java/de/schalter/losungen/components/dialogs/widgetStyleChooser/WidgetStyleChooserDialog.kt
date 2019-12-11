package de.schalter.losungen.components.dialogs.widgetStyleChooser

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.schalter.losungen.R
import de.schalter.losungen.screens.widgetsOverview.WidgetsRecyclerViewAdapter
import de.schalter.losungen.widgets.WidgetContent
import de.schalter.losungen.widgets.WidgetContentType
import de.schalter.losungen.widgets.WidgetData

class WidgetStyleChooserDialog(private val contentToShow: MutableList<WidgetContent>) : DialogFragment(), AdapterView.OnItemClickListener {

    var onStyleSelected: ((style: WidgetStyle) -> Unit)? = null

    private val themes = WidgetStyle.presets

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val dialogView = LayoutInflater.from(it).inflate(R.layout.recycler_view, null)

            val dialog = AlertDialog.Builder(it)
                    .setView(dialogView)
                    .setTitle(R.string.choose_widget_style)
                    .setCancelable(false)
                    .create()

            val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recyclerView)

            val widgetData = themes.map {
                WidgetData(
                        0,
                        it.fontColor,
                        it.backgroundColor,
                        it.fontSize.toInt(),
                        mutableSetOf(WidgetContentType.OLD_TESTAMENT, WidgetContentType.NEW_TESTAMENT),
                        contentToShow)
            }
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            val adapter = WidgetsRecyclerViewAdapter(requireActivity(), widgetData)
            adapter.clickListener = this
            recyclerView.adapter = adapter

            return dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        onStyleSelected?.let { it(themes[position]) }
        this.dismiss()
    }
}