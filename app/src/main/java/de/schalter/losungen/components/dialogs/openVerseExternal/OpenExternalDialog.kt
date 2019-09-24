package de.schalter.losungen.components.dialogs.openVerseExternal

import android.content.Context
import android.preference.PreferenceManager
import android.view.View
import android.widget.CheckBox
import de.schalter.losungen.R
import de.schalter.losungen.firebase.FirebaseUtil
import de.schalter.losungen.utils.PreferenceTags
import de.schalter.losungen.utils.openExternal.BibleVerse
import de.schalter.losungen.utils.openExternal.OpenBibleserver
import de.schalter.losungen.utils.openExternal.OpenExternal
import de.schalter.losungen.utils.openExternal.OpenQuickBible


class OpenExternalDialog(val context: Context) {

    private val externalTools = listOf(
            OpenBibleserver(context),
            OpenQuickBible(context)
    )

    private val availableExternalTools = mutableListOf<OpenExternal>()
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    init {
        for (tool in externalTools) {
            if (tool.isAvailable()) {
                availableExternalTools.add(tool)
            }
        }
    }

    fun open(bibleVerse: BibleVerse) {
        if (availableExternalTools.size == 1) {
            FirebaseUtil.trackOpenExternal(context, availableExternalTools[0].getTitle(), true)
            availableExternalTools[0].open(bibleVerse)
        } else {
            var openedWithDefault = false

            val defaultOption = sharedPreferences.getString(PreferenceTags.OPEN_EXTERNAL_DEFAULT, null)
            if (defaultOption != null) {
                for (tool in availableExternalTools) {
                    if (tool.getTitle() == defaultOption) {
                        openedWithDefault = true
                        FirebaseUtil.trackOpenExternal(context, tool.getTitle(), true)
                        tool.open(bibleVerse)
                        break
                    }
                }
            }

            if (!openedWithDefault) {
                val items = availableExternalTools.map { tool -> tool.getTitle() }
                var saveChoice = false

                val checkBoxView = View.inflate(context, R.layout.list_with_checkbox, null)
                val checkBox = checkBoxView.findViewById<CheckBox>(R.id.checkbox)
                checkBox.setOnCheckedChangeListener { _, isChecked -> saveChoice = isChecked }
                checkBox.text = context.resources.getString(R.string.remember_my_decision)

                val builder = androidx.appcompat.app.AlertDialog.Builder(context)
                builder.setTitle(R.string.open_verse_external)
                builder.setCancelable(true)
                builder.setItems(items.toTypedArray()) { _, which ->
                    if (saveChoice) {
                        val editor = sharedPreferences.edit()
                        editor.putString(PreferenceTags.OPEN_EXTERNAL_DEFAULT, availableExternalTools[which].getTitle())
                        editor.apply()
                    }

                    FirebaseUtil.trackOpenExternal(context, availableExternalTools[which].getTitle(), false)
                    availableExternalTools[which].open(bibleVerse)
                }

                builder.setView(checkBoxView)
                builder.show()
            }
        }
    }

}