package de.schalter.losungen.migration

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * copied from the old legacy project and deleted unused methods
 */
class LegacyDBHandler internal constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        var createDB = "create table if not exists " + TABLE_LOSUNGEN + " ( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_LOSUNGSTEXT + " TEXT, " +
                KEY_LOSUNGSVERS + " TEXT, " +
                KEY_LEHRTEXT + " TEXT, " +
                KEY_LEHRTEXTVERS + " TEXT, " +
                KEY_SONNTAGNAME + " TEXT, " +
                KEY_DATUM + " INTEGER, " +
                KEY_MARKIERT + " INTEGER," +
                KEY_NOTIZENLOSUNG + " TEXT, " +
                KEY_NOTIZENLEHRTEXT + " TEXT, " +
                KEY_AUDIOLOSUNG + " TEXT, " +
                KEY_AUDIOLEHRTEXT + "TEXT);"

        db.execSQL(createDB)

        createDB = "create table if not exists " + TABLE_MONTH + " ( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_LOSUNGSTEXT + " TEXT, " +
                KEY_LOSUNGSVERS + " TEXT, " +
                KEY_DATUM + " INTEGER, " +
                KEY_MARKIERT + " INTEGER, " +
                KEY_NOTIZENLOSUNG + " TEXT, " +
                KEY_AUDIOLOSUNG + " TEXT, " +
                KEY_MONTH_TITLE + " INTEGER);"

        db.execSQL(createDB)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    fun getAllMonthlyVerses(): List<LegacyLosung> {
        val listVerses = mutableListOf<LegacyLosung>()

        val db = this.readableDatabase
        val select = "Select * from $TABLE_MONTH WHERE $KEY_MONTH_TITLE = 1"
        val c = db.rawQuery(select, null)

        if (c.moveToFirst()) {
            do {
                val losung = LegacyLosung()
                losung.losungstext = c.getString(1)
                losung.losungsvers = c.getString(2)
                losung.lehrtext = ""
                losung.lehrtextVers = ""
                losung.sundayName = ""
                losung.date = c.getLong(3)
                losung.isMarked = c.getInt(4) == 1
                losung.notesLosung = c.getString(5)
                losung.notesLehrtext = ""

                listVerses.add(losung)
            } while (c.moveToNext())
        }

        c.close()

        return listVerses
    }

    fun getAllWeeklyVerses(): List<LegacyLosung> {
        val listVerses = mutableListOf<LegacyLosung>()

        val db = this.readableDatabase
        val select = "Select * from $TABLE_MONTH WHERE $KEY_MONTH_TITLE = 0"
        val c = db.rawQuery(select, null)

        if (c.moveToFirst()) {
            do {
                val losung = LegacyLosung()
                losung.losungstext = c.getString(1)
                losung.losungsvers = c.getString(2)
                losung.lehrtext = ""
                losung.lehrtextVers = ""
                losung.sundayName = ""
                losung.date = c.getLong(3)
                losung.isMarked = c.getInt(4) == 1
                losung.notesLosung = c.getString(5)
                losung.notesLehrtext = ""

                listVerses.add(losung)
            } while (c.moveToNext())
        }

        c.close()

        return listVerses
    }

    fun getAllDailyWords(): List<LegacyLosung> {
        val listVerses = mutableListOf<LegacyLosung>()

        val db = this.readableDatabase
        val select = "Select * FROM $TABLE_LOSUNGEN"
        val c = db.rawQuery(select, null)

        if (c.moveToFirst()) {
            do {
                val losung = LegacyLosung()
                losung.losungstext = c.getString(1)
                losung.losungsvers = c.getString(2)
                losung.lehrtext = c.getString(3)
                losung.lehrtextVers = c.getString(4)
                losung.sundayName = c.getString(5)
                losung.date = c.getLong(6)
                losung.isMarked = c.getInt(7) == 1
                losung.notesLosung = c.getString(8)
                losung.notesLehrtext = c.getString(9)

                listVerses.add(losung)
            } while (c.moveToNext())
        }

        c.close()

        return listVerses
    }

    fun getAllDailyWordsWithAudio(): List<LegacyLosung> {
        val listVerses = mutableListOf<LegacyLosung>()

        val db = this.readableDatabase
        val select = "Select $KEY_AUDIOLOSUNG FROM $TABLE_LOSUNGEN WHERE ($KEY_AUDIOLOSUNG IS NOT NULL)"
        val c = db.rawQuery(select, null)

        if (c.moveToFirst()) {
            do {
                val losung = LegacyLosung()
                losung.audioPath = c.getString(0)

                listVerses.add(losung)
            } while (c.moveToNext())
        }

        c.close()

        return listVerses
    }

    fun deleteDatabases(context: Context) {
        context.deleteDatabase(DATABASE_NAME)
    }

    companion object {

        private const val DATABASE_NAME = "losungen"
        private const val DATABASE_VERSION = 3

        private const val TABLE_LOSUNGEN = "losungen"
        private const val TABLE_MONTH = "monthly"
        private const val KEY_ID = "id"
        private const val KEY_MONTH_TITLE = "monthtitle"
        private const val KEY_LOSUNGSTEXT = "losungstext"
        private const val KEY_LOSUNGSVERS = "losungsvers"
        private const val KEY_LEHRTEXT = "lehrtext"
        private const val KEY_LEHRTEXTVERS = "lehrtextvers"
        private const val KEY_SONNTAGNAME = "sonntagname"
        private const val KEY_DATUM = "datum" //at 12 am
        private const val KEY_MARKIERT = "markiert"
        private const val KEY_NOTIZENLOSUNG = "notizenlosung"
        private const val KEY_NOTIZENLEHRTEXT = "notizenlehrtext"
        private const val KEY_AUDIOLOSUNG = "audiolosung"
        private const val KEY_AUDIOLEHRTEXT = "audiolehrtext" //Not used
    }
}
