package de.schalter.losungen2.dataAccess

import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun languageToString(language: Language?): String? {
        return language?.languageCode
    }

    @TypeConverter
    fun stringToLanguage(languageCode: String?): Language? {
        return languageCode?.let { Language.valueOf(languageCode.toUpperCase()) }
    }
}