package schalter.de.losungen2.utils

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import schalter.de.losungen2.R
import schalter.de.losungen2.dataAccess.daily.DailyVerse

data class TextList(val titleInDialog: String, val text: String)

object Share {
    fun text(context: Context, text: String) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_TEXT, text)

        val finalIntent = Intent.createChooser(sharingIntent, context.resources.getString(R.string.share))
        finalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(finalIntent)
    }

    fun textListDialog(context: Context, items: List<TextList>) {
        val itemTitlesDialog = items.map { it.titleInDialog }

        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.share)
        builder.setCancelable(true)
        builder.setItems(itemTitlesDialog.toTypedArray()) { _, which ->
            Share.text(context, items[which].text)
        }.create().show()
    }

    fun dailyVerse(context: Context, dailyVerse: DailyVerse) {
        val textLists = mutableListOf<TextList>()

        textLists.add(
                TextList(
                        context.getString(R.string.old_testament_card_title),
                        dailyVerse.oldTestamentVerseText + System.getProperty("line.separator") + dailyVerse.oldTestamentVerseBible
                ))

        textLists.add(
                TextList(
                        context.getString(R.string.new_testament_card_title),
                        dailyVerse.newTestamentVerseText + System.getProperty("line.separator") + dailyVerse.newTestamentVerseBible
                ))

        textLists.add(
                TextList(
                        context.getString(R.string.old_new_testament_title),
                        dailyVerse.oldTestamentVerseText + System.getProperty("line.separator") + dailyVerse.oldTestamentVerseBible +
                                System.getProperty("line.separator") + System.getProperty("line.separator") +
                                dailyVerse.newTestamentVerseText + System.getProperty("line.separator") + dailyVerse.newTestamentVerseBible
                ))

        textListDialog(context, textLists)
    }
}