package de.schalter.losungen.components.dialogs.widgetStyleChooser

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import androidx.fragment.app.DialogFragment
import de.schalter.losungen.R

class WidgetStyleChooserDialog(private val textToShow: String) : DialogFragment(), AdapterView.OnItemClickListener {

    var onStyleSelected: ((style: WidgetStyle) -> Unit)? = null

    private lateinit var themes: List<WidgetStyle>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val dialogView = LayoutInflater.from(it).inflate(R.layout.dialog_widget_picker_grid, null)

            val dialog = AlertDialog.Builder(it)
                    .setView(dialogView)
                    .setTitle(R.string.choose_widget_style)
                    .setCancelable(false)
                    .create()

            val gridView = dialogView.findViewById<GridView>(R.id.gridView)
            gridView.onItemClickListener = this

            themes = WidgetStyle.presets
            gridView.adapter = WidgetStyleChooserDialogGridAdapter(themes, textToShow)

            return dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        onStyleSelected?.let { it(themes[position]) }
        this.dismiss()
    }
}