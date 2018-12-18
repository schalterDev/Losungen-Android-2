package schalter.de.losungen2.dataAccess

import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 12)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.time.time
    }

    @TypeConverter
    fun languageToString(language: Language?): String? {
        return language?.languageCode
    }

    @TypeConverter
    fun stringToLanguage(languageCode: String?): Language? {
        return languageCode?.let { Language.valueOf(languageCode) }
    }
}