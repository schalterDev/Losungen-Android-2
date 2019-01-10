package schalter.de.losungen2.xmlProcessing

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import schalter.de.losungen2.dataAccess.Language
import schalter.de.losungen2.dataAccess.daily.DailyVerse
import java.io.IOException
import java.io.InputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

private val namespace: String? = null
private const val DAILY_VERSE_START_TAG = "Losungen"
private const val DAILY_VERSE_OLD_TESTAMENT_TEXT = "Losungstext"
private const val DAILY_VERSE_OLD_TESTAMENT_BIBLE = "Losungsvers"
private const val DAILY_VERSE_NEW_TESTAMENT_TEXT = "Lehrtext"
private const val DAILY_VERSE_NEW_TESTAMENT_BIBLE = "Lehrtextvers"
private const val DAILY_VERSE_DATE = "Datum"
private const val DAILY_VERSE_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss"

class LosungenXmlParser(private val language: Language) {

    @Throws(ParseException::class, IOException::class, XmlPullParserException::class)
    fun parseDailyVerseXml(input: InputStream): List<DailyVerse> {
        val dailyVerseList: MutableList<DailyVerse> = mutableListOf()

        val xmlParser: XmlPullParser = Xml.newPullParser()
        xmlParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        xmlParser.setInput(input, null)
        xmlParser.nextTag()

        while (xmlParser.next() != XmlPullParser.END_TAG) {
            if (xmlParser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            val tagName = xmlParser.name
            if (tagName == DAILY_VERSE_START_TAG) {
                dailyVerseList.add(readDailyVerseEntry(xmlParser))
            }
        }

        return dailyVerseList
    }

    @Throws(ParseException::class)
    private fun readDailyVerseEntry(xmlParser: XmlPullParser): DailyVerse {
        xmlParser.require(XmlPullParser.START_TAG, namespace, DAILY_VERSE_START_TAG)

        var oldTestamentText: String? = null
        var oldTestamentBible: String? = null
        var newTestamentText: String? = null
        var newTestamentBible: String? = null
        var dateString: String? = null

        while (xmlParser.next() != XmlPullParser.END_TAG) {
            if (xmlParser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            val name = xmlParser.name
            when (name) {
                DAILY_VERSE_OLD_TESTAMENT_TEXT -> oldTestamentText = readTitle(xmlParser, DAILY_VERSE_OLD_TESTAMENT_TEXT)
                DAILY_VERSE_OLD_TESTAMENT_BIBLE -> oldTestamentBible = readTitle(xmlParser, DAILY_VERSE_OLD_TESTAMENT_BIBLE)
                DAILY_VERSE_NEW_TESTAMENT_TEXT -> newTestamentText = readTitle(xmlParser, DAILY_VERSE_NEW_TESTAMENT_TEXT)
                DAILY_VERSE_NEW_TESTAMENT_BIBLE -> newTestamentBible = readTitle(xmlParser, DAILY_VERSE_NEW_TESTAMENT_BIBLE)
                DAILY_VERSE_DATE -> dateString = readTitle(xmlParser, DAILY_VERSE_DATE)
                else -> skip(xmlParser)
            }
        }

        if (dateString == null || oldTestamentText == null || oldTestamentBible == null || newTestamentText == null || newTestamentBible == null) {
            throw ParseException("could not parse daily verse", 0)
        }

        return DailyVerse(
                oldTestamentVerseText = oldTestamentText,
                oldTestamentVerseBible = oldTestamentBible,
                newTestamentVerseText = newTestamentText,
                newTestamentVerseBible = newTestamentBible,
                date = parseDate(DAILY_VERSE_DATE_PATTERN, dateString),
                language = language)
    }

    @Throws(ParseException::class)
    private fun parseDate(pattern: String, dateString: String): Date {
        val df = SimpleDateFormat(pattern, Locale.GERMANY)
        return df.parse(dateString)
    }

    // Processes title tags in the feed.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readTitle(parser: XmlPullParser, title: String): String {
        parser.require(XmlPullParser.START_TAG, namespace, title)
        val titleString = readText(parser)
        parser.require(XmlPullParser.END_TAG, namespace, title)
        return titleString
    }

    // For the tags title and summary, extracts their text values.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}