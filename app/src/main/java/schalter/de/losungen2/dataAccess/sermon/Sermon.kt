package schalter.de.losungen2.dataAccess.sermon

import androidx.room.*
import schalter.de.losungen2.dataAccess.daily.DailyVerse

@Entity(foreignKeys = [
    ForeignKey(
            entity = DailyVerse::class,
            parentColumns = arrayOf("daily_verse_id"),
            childColumns = arrayOf("daily_verse_id"),
            onDelete = ForeignKey.CASCADE
    )],
        indices = [Index(value = ["daily_verse_id"])])
data class Sermon(
        @PrimaryKey(autoGenerate = true) var sermonId: Int? = null,
        @ColumnInfo(name = "daily_verse_id") var dailyVerseId: Int,
        @ColumnInfo(name = "download_url") var downloadUrl: String,
        @ColumnInfo(name = "path_saved") var pathSaved: String,
        @ColumnInfo(name = "verse_bible") var verseInBible: String? = null,
        @ColumnInfo(name = "author") var author: String? = null,
        @ColumnInfo(name = "provider") var provider: String? = null // something like ERF
)