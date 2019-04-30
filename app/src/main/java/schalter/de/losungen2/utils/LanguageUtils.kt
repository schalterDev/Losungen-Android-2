package schalter.de.losungen2.utils

import android.content.Context
import android.text.format.DateFormat
import schalter.de.losungen2.dataAccess.Language
import java.util.*

object LanguageUtils {

    /**
     * Returns the language of the device
     * E.g.: de, en, fr, ...
     */
    fun getDisplayLanguage(): String {
        return Locale.getDefault().language
    }

    /**
     * Return the enum of the language when known
     */
    fun getDisplayLanguageEnum(): Language? {
        return Language.fromString(getDisplayLanguage())
    }

    /**
     * @return default value Locale.ENGLISH,
     */
    fun getDisplayLanguageLocale(): Locale {
        return getDisplayLanguageEnum()?.locale ?: Locale.ENGLISH
    }

    fun is24Hour(context: Context): Boolean {
        return DateFormat.is24HourFormat(context)
    }

}