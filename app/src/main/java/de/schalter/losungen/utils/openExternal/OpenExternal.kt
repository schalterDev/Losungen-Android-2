package de.schalter.losungen.utils.openExternal

interface OpenExternal {
    fun open(bibleVerse: BibleVerse)
    fun isAvailable(): Boolean
    fun getTitle(): String
}