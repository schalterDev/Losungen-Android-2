package schalter.de.losungen2.utils.openExternal

import android.content.Context
import schalter.de.losungen2.utils.Open

const val BASE_URL = "https://www.bibleserver.com/text/"

// TODO (add tests for this class)
class OpenBibleserver(val context: Context) : OpenExternal {
    override fun getTitle(): String {
        return "bibleserver.com"
    }

    override fun isAvailable(): Boolean {
        return true
    }

    private val language = "de"

    override fun open(bibleVerse: BibleVerse) {
        var chapter = ""
        if (bibleVerse.chapter != null) {
            chapter = bibleVerse.chapter.toString() + ","
        }

        val verses = bibleVerse.verses.joinToString(separator = ".")
        val url = BASE_URL + getTranslationForLanguage(language) + "/" +
                bibleVerse.bookInBible.toLocaleString(language) +
                chapter + verses

        Open.website(context, url)
    }

    private fun getTranslationForLanguage(language: String): String {
        when (language) {
            "de" -> return "LUT"
            "en" -> return "ESV"
            "es" -> return "BTX"
            "ar" -> return "ALAB"
            "fr" -> return "BDS"
            "it" -> return "ITA"
            "nl" -> return "HTB"
            "da" -> return "DK"
            "pt" -> return "PRT"
            "no" -> return "NOR" // only NT
            "sv" -> return "BSV"
            "pl" -> return "PSZ" // only NT
            "hr" -> return "CKK" // only NT
            "hu" -> return "HUN" // only NT (KAR OT)
            "ro" -> return "NTR"
            "be" -> return "RSZ"
            "bg" -> return "BLG"
        }

        return "ESV"
    }
}