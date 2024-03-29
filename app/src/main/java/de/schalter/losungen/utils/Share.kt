package de.schalter.losungen.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import de.schalter.losungen.R
import de.schalter.losungen.components.exceptions.TranslatableException
import de.schalter.losungen.dataAccess.daily.DailyVerse
import de.schalter.losungen.firebase.FirebaseUtil
import de.schalter.losungen.sermon.sermonProvider.SermonProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

data class TextList(val titleInDialog: String, val text: String, val subject: String)

object Share {
    fun text(context: Context, text: String, title: String? = null) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_TEXT, text)

        title?.let { sharingIntent.putExtra(Intent.EXTRA_SUBJECT, it) }

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
            text(context, items[which].text, items[which].subject)
        }.create().show()
    }

    fun dailyVerse(context: Context, dailyVerse: DailyVerse) {
        FirebaseUtil.trackVerseShared(context)

        val textLists = mutableListOf<TextList>()

        val dateString = SimpleDateFormat("E, dd.MM", LanguageUtils.getDisplayLanguageLocale()).format(dailyVerse.date)
        val subject = context.resources.getString(R.string.daily_word_from, dateString)

        textLists.add(
                TextList(
                        context.getString(R.string.old_testament_card_title),
                        dailyVerse.oldTestamentVerseText + System.getProperty("line.separator") + dailyVerse.oldTestamentVerseBible,
                        subject
                ))

        textLists.add(
                TextList(
                        context.getString(R.string.new_testament_card_title),
                        dailyVerse.newTestamentVerseText + System.getProperty("line.separator") + dailyVerse.newTestamentVerseBible,
                        subject
                ))

        textLists.add(
                TextList(
                        context.getString(R.string.old_new_testament_title),
                        dailyVerse.oldTestamentVerseText + System.getProperty("line.separator") + dailyVerse.oldTestamentVerseBible +
                                System.getProperty("line.separator") + System.getProperty("line.separator") +
                                dailyVerse.newTestamentVerseText + System.getProperty("line.separator") + dailyVerse.newTestamentVerseBible,
                        subject
                ))

        textListDialog(context, textLists)
    }

    @SuppressLint("CheckResult")
    fun sermon(context: Context, date: Date) {
        Toast.makeText(context, R.string.loading_sermon, Toast.LENGTH_SHORT).show()
        SermonProvider.getImplementation(context).getIfExistsOrLoadAndSave(date)
                .subscribe(
                        { sermon ->
                            mp3File(context, sermon.pathSaved)
                        },
                        { error ->
                            AsyncUtils.runOnMainThread {
                                val errorMessage = if (error is TranslatableException) {
                                    error.getStringForUser(context)
                                } else {
                                    error.message ?: ""
                                }

                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                            }
                        })
    }

    fun mp3File(context: Context, path: String) {
        FirebaseUtil.trackSermonShared(context, true)

        val shareUri = FileProvider.getUriForFile(context, "de.schalter.losungen.file.provider", File(path))

        val intentAudios = Intent()
        intentAudios.action = Intent.ACTION_SEND
        val resInfoList = context.packageManager.queryIntentActivities(intentAudios, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            context.grantUriPermission(packageName, shareUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val share = Intent(Intent.ACTION_SEND)
        share.type = "audio/*"
        share.putExtra(Intent.EXTRA_STREAM, shareUri)

        context.startActivity(Intent.createChooser(share, context.resources.getString(R.string.share_sermon)))
    }
}