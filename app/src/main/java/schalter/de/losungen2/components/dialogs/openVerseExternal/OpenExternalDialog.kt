package schalter.de.losungen2.components.dialogs.openVerseExternal

import android.content.Context
import schalter.de.losungen2.R
import schalter.de.losungen2.utils.openExternal.BibleVerse
import schalter.de.losungen2.utils.openExternal.OpenBibleserver
import schalter.de.losungen2.utils.openExternal.OpenExternal
import schalter.de.losungen2.utils.openExternal.OpenQuickBible

// TODO add test for this class
class OpenExternalDialog(val context: Context) {

    private val externalTools = listOf(
            OpenBibleserver(context),
            OpenQuickBible(context)
    )

    private val availableExternalTools = mutableListOf<OpenExternal>()

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
            val items = availableExternalTools.map { tool -> tool.getTitle() }

            val builder = androidx.appcompat.app.AlertDialog.Builder(context)
            builder.setTitle(R.string.open_verse_external)
            builder.setCancelable(true)
            builder.setItems(items.toTypedArray()) { _, which ->
                availableExternalTools[which].open(bibleVerse)
            }
            builder.show()
        }
    }

}