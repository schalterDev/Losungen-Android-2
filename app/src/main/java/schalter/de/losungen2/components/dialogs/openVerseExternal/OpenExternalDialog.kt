package schalter.de.losungen2.components.dialogs.openVerseExternal

import android.content.Context
import android.preference.PreferenceManager
import android.view.View
import android.widget.CheckBox
import schalter.de.losungen2.R
import schalter.de.losungen2.utils.PreferenceTags
import schalter.de.losungen2.utils.openExternal.BibleVerse
import schalter.de.losungen2.utils.openExternal.OpenBibleserver
import schalter.de.losungen2.utils.openExternal.OpenExternal
import schalter.de.losungen2.utils.openExternal.OpenQuickBible


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
            availableExternalTools[0].open(bibleVerse)
        } else {
            var openedWithDefault = false

            val defaultOption = sharedPreferences.getString(PreferenceTags.OPEN_EXTERNAL_DEFAULT, null)
            if (defaultOption != null) {
                for (tool in availableExternalTools) {
                    if (tool.getTitle() == defaultOption) {
                        openedWithDefault = true
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

                    availableExternalTools[which].open(bibleVerse)
                }

                builder.setView(checkBoxView)
                builder.show()
            }
        }
    }

}