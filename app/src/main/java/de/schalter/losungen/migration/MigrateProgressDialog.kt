package de.schalter.losungen.migration

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import de.schalter.losungen.R
import de.schalter.losungen.utils.CoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MigrateProgressDialog(private val text1Resource: Int?,
                            private val text2Resource: Int?,
                            private val showFinishButton: Boolean = true) : DialogFragment(), CoroutineScope, Migration.OnProgressChanged {

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = CoroutineDispatchers.Background + job

    private lateinit var mActivity: Activity

    private lateinit var dialog: AlertDialog
    private lateinit var textViewProgress: TextView
    private lateinit var progressBarContainer: View
    private var dialogButton: Button? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            mActivity = activity

            val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_migrate, null)

            val dialogBuilder = AlertDialog.Builder(activity)
                    .setView(dialogView)
                    .setCancelable(false)
            if (showFinishButton) {
                dialogBuilder.setPositiveButton(R.string.finish) { _, _ ->
                    closeDialogAndRecreateActivity()
                }
            }

            dialog = dialogBuilder.create()

            progressBarContainer = dialogView.findViewById(R.id.progressBarContainer)
            textViewProgress = dialogView.findViewById(R.id.migrate_step_textView)
            dialogView.findViewById<TextView>(R.id.migrationText1).let { textView ->
                if (this.text1Resource != null) {
                    textView.text = context?.getString(text1Resource)
                } else {
                    textView.visibility = View.GONE
                }
            }
            dialogView.findViewById<TextView>(R.id.migrationText2).let { textView ->
                if (this.text2Resource != null) {
                    textView.text = context?.getString(text2Resource)
                } else {
                    textView.visibility = View.GONE
                }
            }

            dialog.setOnShowListener {
                launch(CoroutineDispatchers.Ui) {
                    dialogButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    dialogButton?.isEnabled = false
                }
            }

            return dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun progressChanged(step: String) {
        launch(CoroutineDispatchers.Ui) {
            textViewProgress.text = step
        }
    }

    override fun finished() {
        if (this.showFinishButton) {
            launch(CoroutineDispatchers.Ui) {
                dialogButton?.isEnabled = true
                progressBarContainer.visibility = View.GONE
            }
        } else {
            closeDialogAndRecreateActivity()
        }
    }

    private fun closeDialogAndRecreateActivity() {
        launch(CoroutineDispatchers.Ui) {
            dismiss()
            mActivity.recreate()
        }
    }
}