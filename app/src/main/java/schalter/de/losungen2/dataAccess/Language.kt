package schalter.de.losungen2.dataAccess

enum class Language(val languageCode: String) {
    DE("DE") {
        override val longString = "Deutsch"
        override fun toLongString() = longString
    },
    EN("EN") {
        override val longString = "English"
        override fun toLongString() = longString
    };

    abstract val longString: String
    abstract fun toLongString(): String

    companion object {
        fun fromString(value: String): Language? {
            return when (value) {
                DE.toString() -> DE
                DE.longString -> DE
                EN.toString() -> EN
                EN.longString -> EN
                else -> null
            }
        }
    }
}