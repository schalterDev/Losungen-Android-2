package de.schalter.losungen.components.dialogs.deleteSermons

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import de.schalter.losungen.R
import de.schalter.losungen.dataAccess.VersesDatabase
import de.schalter.losungen.dataAccess.daily.DailyVerse
import de.schalter.losungen.utils.AsyncUtils
import de.schalter.losungen.utils.LanguageUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DeleteSermonsDialog(context: Context) : DialogFragment() {

    private val versesDatabase = VersesDatabase.provideVerseDatabase(context)

    private var entriesClicked: BooleanArray? = null
    private var entryDates: LongArray? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { context ->
            val builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.delete_sermons)
            builder.setCancelable(true)
            builder.setPositiveButton(R.string.delete_sermons) { _, _ ->
                onDialogClosed(true)
            }
            builder.setNegativeButton(R.string.cancel) { _, _ ->
                onDialogClosed(false)
            }
            builder.setMultiChoiceItems(getEntries(), entriesClicked) { _, which, isChecked ->
                entriesClicked?.set(which, isChecked)
            }

            return builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            entriesClicked?.forEachIndexed { index, checked ->
                if (checked) {
                    deleteDate(entryDates!![index])
                }
            }
        }
    }

    private fun getEntries(): Array<CharSequence> {
        getEntryValuesLong().let { entryDates ->
            val entries = ArrayList<CharSequence>()

            entryDates.forEach { date ->
                val dateFormatter = SimpleDateFormat(DATE_FORMAT, LanguageUtils.getDisplayLanguageLocale())
                entries.add(dateFormatter.format(date))
            }
            entriesClicked = BooleanArray(entries.size)
            return entries.toTypedArray()
        }
    }

    private fun getEntryValuesLong(): LongArray {
        var versesWithSermon: Array<DailyVerse>? = null
        // TODO change this to work asynchronous. For this the dialog has to be
        // rewritten to have a loading indicator and then shows the items
        Thread {
            versesWithSermon = versesDatabase.dailyVerseDao().getAllDailyVersesWithSermon()
        }.apply {
            this.start()
            this.join()
        }

        entryDates = versesWithSermon?.map { it.date.time }?.toLongArray().apply {
            this?.sort()
        }
        return entryDates!!
    }

    private fun deleteDate(date: Long) {
        GlobalScope.launch {
            val sermons = AsyncUtils.liveDataToSingleOptional(versesDatabase.sermonDao().getSermonsForDate(DailyVerse.getDateForDay(Date(date)))).blockingGet().value!!

            sermons.forEach { sermon ->
                File(sermon.pathSaved).delete()
                versesDatabase.sermonDao().deleteSermon(sermon)
            }
        }
    }

    companion object {
        private const val DATE_FORMAT = "E dd.MM"
    }
}