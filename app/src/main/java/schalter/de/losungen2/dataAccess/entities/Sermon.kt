package schalter.de.losungen2.dataAccess.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = arrayOf(ForeignKey(
        entity = DailyVerse::class,
        parentColumns = arrayOf("daily_verse_id"),
        childColumns = arrayOf("daily_verse_id"))
))
data class Sermon(
        @PrimaryKey(autoGenerate = true) var sermonId: Int,
        @ColumnInfo(name = "daily_verse_id") var dailyVerseId: Int,
        @ColumnInfo(name = "download_url") var downloadUrl: String,
        @ColumnInfo(name = "path_saved") var pathSaved: String,
        @ColumnInfo(name = "verse_bible") var verseInBible: String?,
        @ColumnInfo(name = "author") var author: String?
)