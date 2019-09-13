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

class MigrateProgressDialog : DialogFragment(), CoroutineScope {

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

            dialog = AlertDialog.Builder(activity)
                    .setView(dialogView)
                    .setCancelable(false)
                    .setPositiveButton(R.string.finish) { _, _ ->
                        onFinish()
                    }
                    .create()

            progressBarContainer = dialogView.findViewById(R.id.progressBarContainer)
            textViewProgress = dialogView.findViewById(R.id.migrate_step_textView)

            dialog.setOnShowListener {
                launch(CoroutineDispatchers.Ui) {
                    dialogButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    dialogButton?.isEnabled = false
                }
                start()
            }

            return dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun start() {
        launch {
            val migration = Migration(mActivity)
            migration.progressChangeListener = object : Migration.OnProgressChanged {
                override fun finished() {
                    launch(CoroutineDispatchers.Ui) {
                        dialogButton?.isEnabled = true
                        progressBarContainer.visibility = View.GONE
                    }
                }

                override fun progressChanged(step: String) = onProgress(step)
            }

            migration.migrateFromLegacyIfNecessary()
        }
    }

    private fun onProgress(step: String) {
        launch(CoroutineDispatchers.Ui) {
            textViewProgress.text = step
        }
    }

    private fun onFinish() {
        launch(CoroutineDispatchers.Ui) {
            dismiss()
            mActivity.recreate()
        }
    }
}