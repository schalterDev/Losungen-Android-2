package schalter.de.losungen2.utils.openExternal

interface OpenExternal {
    fun open(bibleVerse: BibleVerse)
    fun isAvailable(): Boolean
    fun getTitle(): String
}