package schalter.de.losungen2.dataAccess

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class DailyVerse(
        @ColumnInfo(name = "daily_verse_id") @PrimaryKey(autoGenerate = true) var dailyVerseId: Int? = null,
        // Date is automatic converted to 12am 0 minutes 0 seconds 0 milliseconds
        @ColumnInfo(name = "date") var date: Date,
        @ColumnInfo(name = "old_testament_verse_text") var oldTestamentVerseText: String,
        @ColumnInfo(name = "old_testament_verse_bible") var oldTestamentVerseBible: String,
        @ColumnInfo(name = "new_testament_verse_text") var newTestamentVerseText: String,
        @ColumnInfo(name = "new_testament_verse_bible") var newTestamentVerseBible: String,
        @ColumnInfo(name = "is_favourite") var isFavourite: Boolean = false,
        @ColumnInfo(name = "notes") var notes: String? = null,
        @ColumnInfo(name = "language") var language: Language
)

enum class Language (val languageCode: String) {
    DE("DE"),
    EN("EN")
}