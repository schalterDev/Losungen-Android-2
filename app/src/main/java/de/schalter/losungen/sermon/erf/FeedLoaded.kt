package de.schalter.losungen.sermon.erf

data class FeedLoaded(
        val downloadPath: String,
        val author: String? = null,
        val bibleVerse: String? = null)