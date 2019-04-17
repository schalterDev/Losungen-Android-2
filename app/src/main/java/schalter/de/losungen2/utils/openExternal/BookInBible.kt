package schalter.de.losungen2.utils.openExternal

enum class BookInBible(val bookNumber: Int) {
    GENESIS(1),
    EXODUS(2),
    LEVITICUS(3),
    NUMBERS(4),
    DEUTERONOMY(5),
    JOSHUA(6),
    JUDGES(7),
    RUTH(8),
    SAMUEL1(9),
    SAMUEL2(10),
    KINGS1(11),
    KINGS2(12),
    CHRONICLES1(13),
    CHRONICLES2(14),
    EZRA(15),
    NEHEMIAH(16),
    ESTHER(17),
    JOB(18),
    PSALMS(19),
    PROVERBS(20),
    ECCLESIASTES(21),
    SONG_OF_SALOMON(22),
    ISAIAH(23),
    JEREMIAH(24),
    LAMENTATIONS(25),
    EZEKIEL(26),
    DANIEL(27),
    HOSEA(28),
    JOEL(29),
    AMOS(30),
    OBADIAH(31),
    JONAH(32),
    MICAH(33),
    NAHUM(34),
    HABAKKUK(35),
    ZEPHANIAH(36),
    HAGGAI(37),
    ZECHARIAH(38),
    MALACHI(39),
    MATTHEW(40),
    MARK(41),
    LUKE(42),
    JOHN(43),
    ACTS(44),
    ROMANS(45),
    CORINTHIANS1(46),
    CORINTHIANS2(47),
    GALATIANS(48),
    EPHESIANS(49),
    PHILIPPIANS(50),
    COLOSSIANS(51),
    THESSALONIANS1(52),
    THESSALONIANS2(53),
    TIMOTHY1(54),
    TIMOTHY2(55),
    TITUS(56),
    PHILEMON(57),
    HEBREWS(58),
    JAMES(59),
    PETER1(60),
    PETER2(61),
    JOHN1(62),
    JOHN2(63),
    JOHN3(64),
    JUDE(65),
    REVELATION(66);

    fun toLocaleString(language: String): String {
        when (language) {
            "de" -> return GERMAN_BOOK_NAMES[bookNumber - 1][0]
        }

        return GERMAN_BOOK_NAMES[bookNumber - 1][0]
    }

    companion object {
        private val map = values().associateBy(BookInBible::bookNumber)
        fun fromInt(type: Int) = map[type]

        private val GERMAN_BOOK_NAMES = arrayOf(
                arrayOf("1mose", "genesis"),
                arrayOf("2mose", "exodus"),
                arrayOf("3mose", "levitikus"),
                arrayOf("4mose", "numeri"),
                arrayOf("5mose", "deuteronomium"),
                arrayOf("josua"),
                arrayOf("richter"),
                arrayOf("rut"),
                arrayOf("1samuel"),
                arrayOf("2samuel"),
                arrayOf("1könige"),
                arrayOf("2könige"),
                arrayOf("1chronik"),
                arrayOf("2chronik"),
                arrayOf("esra"),
                arrayOf("nehemia"),
                arrayOf("ester"),
                arrayOf("hiob", "ijob"),
                arrayOf("psalmen", "psalm"),
                arrayOf("sprüche", "songofsalomon"),
                arrayOf("prediger"),
                arrayOf("hoheslied"),
                arrayOf("jesaja"),
                arrayOf("jeremia"),
                arrayOf("klagelieder"),
                arrayOf("hesekiel", "ezechiel"),
                arrayOf("daniel"),
                arrayOf("hosea"),
                arrayOf("joel"),
                arrayOf("amos"),
                arrayOf("obadja"),
                arrayOf("jonas"),
                arrayOf("micha"),
                arrayOf("nahum"),
                arrayOf("habakuk"),
                arrayOf("zefanja"),
                arrayOf("haggai"),
                arrayOf("sacharja"),
                arrayOf("maleachi"),
                arrayOf("matthäus"),
                arrayOf("markus"),
                arrayOf("lukas"),
                arrayOf("johannes"),
                arrayOf("apostelgeschichte"),
                arrayOf("römer"),
                arrayOf("1korinther"),
                arrayOf("2korinther"),
                arrayOf("galater"),
                arrayOf("epheser"),
                arrayOf("philipper"),
                arrayOf("kolosser"),
                arrayOf("1thessalonicher"),
                arrayOf("2thessalonicher"),
                arrayOf("1timotheus"),
                arrayOf("2timotheus"),
                arrayOf("titus"),
                arrayOf("philemon"),
                arrayOf("hebräer"),
                arrayOf("jakobus"),
                arrayOf("1petrus"),
                arrayOf("2petrus"),
                arrayOf("1johannes"),
                arrayOf("2johannes"),
                arrayOf("3johannes"),
                arrayOf("judas"),
                arrayOf("offenbarung")
        )

        private val BOOK_NAMES = GERMAN_BOOK_NAMES

        fun fromString(string: String): BookInBible? {
            val indexFoundBook = BOOK_NAMES.indexOfFirst { names -> names.contains(string) }

            return if (indexFoundBook == -1)
                null
            else
                fromInt(indexFoundBook + 1)
        }
    }
}