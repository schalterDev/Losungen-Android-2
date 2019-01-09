package schalter.de.losungen2.components.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import schalter.de.losungen2.R

class ImportVersesDialog(val context: Context) {

    //    private lateinit var dataManagement: DataManagement
    private lateinit var spinner: Spinner
    private lateinit var dialog: AlertDialog

    init {
        this.load()
    }

    fun show() {
        dialog.show()
    }

    fun close() {
        dialog.cancel()
    }

    private fun load() {
        val li = LayoutInflater.from(context)
        val dialogView = li.inflate(R.layout.dialog_import, null)

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        dialog = builder.create()

//        dataManagement = DataManagement(alertDialog.context)

        initDialog(dialogView)
    }

    private fun initDialog(dialog: View) {
        spinner = dialog.findViewById(R.id.spinner_language)!!

//        dataManagement.getAvailableData(Language.DE).
    }
}