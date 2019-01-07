package schalter.de.losungen2.components.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import schalter.de.losungen2.R

class ImportVersesDialog : DialogFragment() {

    //    private lateinit var dataManagement: DataManagement
    private lateinit var spinner: Spinner

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val li = LayoutInflater.from(context)
            val dialogView = li.inflate(R.layout.dialog_import, null)

            val builder = AlertDialog.Builder(it)
            builder.setView(dialogView)
            val alertDialog = builder.create()

//            dataManagement = DataManagement(alertDialog.context)

            initDialog(dialogView)
            return alertDialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun initDialog(dialog: View) {
        spinner = dialog.findViewById(R.id.spinner_language)!!

//        dataManagement.getAvailableData(Language.DE).
    }
}