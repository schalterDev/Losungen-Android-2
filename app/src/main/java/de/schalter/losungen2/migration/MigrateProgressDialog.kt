package de.schalter.losungen2.migration

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import de.schalter.losungen2.R
import de.schalter.losungen2.utils.CoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MigrateProgressDialog : DialogFragment(), CoroutineScope {

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = CoroutineDispatchers.Background + job

    private lateinit var dialog: AlertDialog
    private lateinit var textViewProgress: TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_migrate, null)

            dialog = AlertDialog.Builder(activity)
                    .setView(dialogView)
                    .setCancelable(false)
                    .create()

            textViewProgress = dialog.findViewById(R.id.migrate_step_textView)!!

            dialog.setOnShowListener {
                start()
            }

            return dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun start() {
        launch {
            val migration = Migration(activity!!)
            migration.progressChangeListener = object : Migration.OnProgressChanged {
                override fun finished() = onFinish()
                override fun progressChanged(step: String) = onProgress(step)
            }

            migration.migrateFromLegacyIfNecessary()
        }
    }

    fun onProgress(step: String) {
        launch(CoroutineDispatchers.Ui) {
            textViewProgress.text = step
        }
    }

    fun onFinish() {
        dismiss()
        activity?.recreate()
    }
}