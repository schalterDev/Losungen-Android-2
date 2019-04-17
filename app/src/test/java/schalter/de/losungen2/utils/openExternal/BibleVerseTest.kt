package schalter.de.losungen2.utils.openExternal

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BibleVerseTest {
    @Test
    fun convertBookWithNumber() {
        val verse = "1. Mose 3,4"
        val result = BibleVerse(verse)

        assertEquals(result.bookInBible, BookInBible.fromInt(1))
        assertEquals(result.chapter, 3)
        assertEquals(result.verses, listOf(4))
    }

    @Test
    fun convertBookWithNumbersAndVerseRange() {
        val verse = "2 Mose 3,4-6"
        val result = BibleVerse(verse)

        assertEquals(result.bookInBible, BookInBible.fromInt(2))
        assertEquals(result.chapter, 3)
        assertEquals(result.verses, listOf(4, 5, 6))
    }

    @Test
    fun convertBookWithNumbersAndVerseEnum() {
        val verse = "3.Mose 3,4.6.8"
        val result = BibleVerse(verse)

        assertEquals(result.bookInBible, BookInBible.fromInt(3))
        assertEquals(result.chapter, 3)
        assertEquals(result.verses, listOf(4, 6, 8))
    }

    @Test
    fun convertBookWithoutChapter() {
        val verse = "Judas 30"
        val result = BibleVerse(verse)

        assertEquals(result.bookInBible, BookInBible.fromString("judas"))
        assertNull(result.chapter)
        assertEquals(result.verses, listOf(30))
    }

    @Test
    fun convertBookWithoutChapterAndVerseEnum() {
        val verse = "Judas 30.35.36"
        val result = BibleVerse(verse)

        assertEquals(result.bookInBible, BookInBible.fromString("judas"))
        assertNull(result.chapter)
        assertEquals(result.verses, listOf(30, 35, 36))
    }

    @Test
    fun convertBookWithoutChapterAndVerseRange() {
        val verse = "Judas 30-35"
        val result = BibleVerse(verse)

        assertEquals(result.bookInBible, BookInBible.fromString("judas"))
        assertNull(result.chapter)
        assertEquals(result.verses, listOf(30, 31, 32, 33, 34, 35))
    }

    @Test
    fun convertBookWithSpaces() {
        val verse = "Song Of Salomon 5,4"
        val result = BibleVerse(verse)

        assertEquals(result.bookInBible, BookInBible.fromString("songofsalomon"))
        assertEquals(result.chapter, 5)
        assertEquals(result.verses, listOf(4))
    }

    @Test(expected = BibleVerseParseException::class)
    fun canNotConvertDotAndDash() {
        val verse = "Matthäus 5,4.5-9"
        BibleVerse(verse)
    }

    @Test(expected = BibleVerseParseException::class)
    fun unknownBook() {
        val verse = "Unknown 4,5"
        BibleVerse(verse)
    }

    @Test(expected = BibleVerseParseException::class)
    fun onlyBook() {
        val verse = "Matthäus"
        BibleVerse(verse)
    }
}