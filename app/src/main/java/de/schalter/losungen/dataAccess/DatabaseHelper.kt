package de.schalter.losungen.dataAccess

import android.content.Context
import de.schalter.losungen.dataAccess.daily.DailyVerse
import de.schalter.losungen.dataAccess.monthly.MonthlyVerse
import de.schalter.losungen.dataAccess.weekly.WeeklyVerse

class DatabaseHelper(val context: Context) {

    private var database: VersesDatabase = VersesDatabase.provideVerseDatabase(context)

    fun importDailyVerses(dailyVerses: List<DailyVerse>) {
        dailyVerses.forEach { database.dailyVerseDao().insertDailyVerse(it) }
    }

    fun importWeeklyVerses(weeklyVerses: List<WeeklyVerse>) {
        weeklyVerses.forEach { database.weeklyVerseDao().insertWeeklyVerse(it) }
    }

    fun importMonthlyVerses(monthlyVerses: List<MonthlyVerse>) {
        monthlyVerses.forEach { database.monthlyVerseDao().insertMonthlyVerse(it) }
    }
}