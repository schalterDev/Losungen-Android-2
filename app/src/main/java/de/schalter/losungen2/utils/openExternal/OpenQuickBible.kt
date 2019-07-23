package de.schalter.losungen2.utils.openExternal

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build


const val MULTIPLIER_BOOK = 65536
const val MULTIPLIER_CHAPTER = 256

/**
 * See https://docs.google.com/document/d/16DCCf49QUgwZD7cKw34ixPFhD-yy30SvWLARp2bjbmk/edit
 * for more info
 */
class OpenQuickBible(val context: Context) : OpenExternal {
    override fun getTitle(): String {
        return "QuickBible"
    }

    override fun isAvailable(): Boolean {
        val testIntent = Intent("yuku.alkitab.action.SHOW_VERSES_DIALOG")

        val mgr = context.packageManager
        val list = mgr.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY)
        return list.size > 0
    }

    override fun open(bibleVerse: BibleVerse) {
        val bookNumber = bibleVerse.bookInBible.bookNumber - 1
        val chapter = bibleVerse.chapter ?: 1

        val versesInAri = mutableListOf<Int>()
        for (verse in bibleVerse.verses) {
            versesInAri.add(bookNumber * MULTIPLIER_BOOK + chapter * MULTIPLIER_CHAPTER + verse)
        }

        val targetForIntent = StringBuilder("a:")
        versesInAri.forEachIndexed { index, verse ->
            targetForIntent.append(verse)
            // if not last element
            if (index != versesInAri.size - 1)
                targetForIntent.append(",")
        }

        val intent = Intent("yuku.alkitab.action.SHOW_VERSES_DIALOG")
        intent.putExtra("target", targetForIntent.toString())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}