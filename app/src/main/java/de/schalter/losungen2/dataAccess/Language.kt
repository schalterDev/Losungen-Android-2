package de.schalter.losungen2.dataAccess

import java.util.*

enum class Language(val languageCode: String) {
    DE("de") {
        override val longString = "Deutsch"
        override val locale: Locale = Locale.GERMAN
    },
    EN("en") {
        override val longString = "English"
        override val locale: Locale = Locale.ENGLISH
    },
    IS("is") {
        override val longString = "Íslenska"
        override val locale: Locale = Locale.ENGLISH
    },
    SP("sp") {
        override val longString = "Español"
        override val locale: Locale = Locale.ENGLISH
    },
    AR("ar") {
        override val longString = "العربية"
        override val locale: Locale = Locale.ENGLISH
    },
    PT("pt") {
        override val longString = "Português"
        override val locale: Locale = Locale.ENGLISH
    },
    FR("fr") {
        override val longString = "Française"
        override val locale: Locale = Locale.FRENCH
    };

    abstract val longString: String
    abstract val locale: Locale

    companion object {
        fun fromString(value: String): Language? {
            for (language in values()) {
                if (language.toString().toLowerCase() == value.toLowerCase() || language.longString == value) {
                    return language
                }
            }

            return null
        }
    }
}