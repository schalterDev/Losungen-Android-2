package schalter.de.losungen2.components.dialogs.importDialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import schalter.de.losungen2.R
import schalter.de.losungen2.dataAccess.Language


class ImportVersesDialog(val context: Context) {

    private lateinit var dataManagement: DataManagement

    private lateinit var spinner: Spinner
    private val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(context, android.R.layout.simple_spinner_item)

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

        initDialog(dialogView)

        dataManagement = DataManagement(dialog.context)

        GlobalScope.launch {
            val dataGerman = dataManagement.getAvailableData(Language.DE)
            if (dataGerman != null) {
                launch(Dispatchers.Main) {
                    spinnerAdapter.addAll(dataGerman.map { element -> element.year.toString() })
                }
            }
        }
    }

    private fun initDialog(dialog: View) {
        spinner = dialog.findViewById(R.id.spinner_language)

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = spinnerAdapter
    }
}