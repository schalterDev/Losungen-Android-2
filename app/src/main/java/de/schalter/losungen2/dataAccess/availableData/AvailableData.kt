package de.schalter.losungen2.dataAccess.availableData

import androidx.room.ColumnInfo
import androidx.room.Entity
import de.schalter.losungen2.dataAccess.Language
import java.util.*

@Entity(primaryKeys = ["year", "language"])
data class AvailableData(
        @ColumnInfo(name = "year") var year: Int,
        @ColumnInfo(name = "language") var language: Language,
        @ColumnInfo(name = "last_updated") var lastUpdated: Date = Date()
)