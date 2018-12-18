package schalter.de.losungen2.dataAccess

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class MonthlyVerse (
        @PrimaryKey(autoGenerate = true) var monthlyVerseId: Int,
        @ColumnInfo(name = "date") var date: Date,
        @ColumnInfo(name = "verse_text") var verseText: String,
        @ColumnInfo(name = "verse_bible") var verseBible: String,
        @ColumnInfo(name = "is_favourite") var isFavourite: Boolean = false,
        @ColumnInfo(name = "notes") var notes: String?
)