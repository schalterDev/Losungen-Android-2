package schalter.de.losungen2.utils.openExternal

/**
 * Parses a string.
 * Possible formats for a string:
 * 1. Mose 3,4
 * 1.Mose 3,4
 * 1.Mose 3,4-5
 * 1.Mose 3,4.6.11
 * Judas 3
 * Judas 3.4
 * Song of Salomon 3,4
 */
class BibleVerse(verseAsString: String) {

    var bookInBible: BookInBible
    var chapter: Int? = null //the book judas only has one chapter
    var verses: List<Int>

    private val REGEX_CHAPTER_VERSE = Regex(" [\\d\\[.,\\-\\]]+")
    private val REGEX_CHAPTER = Regex("\\d+,")

    init {
        // parse book
        var book = verseAsString.replace(REGEX_CHAPTER_VERSE, "")
        var versesAndChapter = verseAsString.replace(book, "")

        versesAndChapter = versesAndChapter.trim()
        book = book.trim().toLowerCase().replace(".", "").replace(Regex(" "), "")

        val parsedBook = BookInBible.fromString(book)
        if (parsedBook == null)
            throw BibleVerseParseException("Could not parse '$book' as a book")
        else
            bookInBible = parsedBook

        // parse chapter: versesAndChapter contains everything except the book
        val verses = versesAndChapter.replace(REGEX_CHAPTER, "")
        var chapter = versesAndChapter.replace(verses, "")
        chapter = chapter.replace(",", "")

        if (chapter != "")
            this.chapter = chapter.toInt()

        // parse verses. Can be: "3", "3-4", "3.4.5", "4-6.8" (this will throw an error)
        // contains no "." or "-"
        try {
            if (!verses.contains(Regex("[.\\-]"))) {
                this.verses = listOf(verses.toInt())
            } else if (verses.contains(Regex("[.\\-]\\d+-|-\\d+\\."))) { // contains both "." and "-" or two "-"
                throw BibleVerseParseException("Can not parse verses with '.' and '-' or two '-'")
            } else if (verses.contains('.')) {
                // add all verses separated by a "." to the list
                this.verses = verses.split(".").map { verse -> verse.toInt() }
            } else if (verses.contains('-')) {
                // add all verses between the two number (inclusive) to the list
                val versesBorders = verses.split("-")
                val verseList = mutableListOf<Int>()
                for (i in versesBorders[0].toInt()..versesBorders[1].toInt()) {
                    verseList.add(i)
                }
                this.verses = verseList
            } else {
                throw BibleVerseParseException("Unknown error: verses don't contain '.' or '-' but they should")
            }
        } catch (e: NumberFormatException) {
            throw BibleVerseParseException("Tried to parse number: " + e.message)
        }
    }
}