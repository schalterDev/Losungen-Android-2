package schalter.de.losungen2.dataAccess

import android.content.Context
import schalter.de.losungen2.dataAccess.daily.DailyVerse
import schalter.de.losungen2.dataAccess.monthly.MonthlyVerse
import schalter.de.losungen2.dataAccess.weekly.WeeklyVerse

class DatabaseHelper(val context: Context) {

    private var database: VersesDatabase = VersesDatabase.provideVerseDatabase(context)

    fun importAllVerses(dailyVerses: List<DailyVerse>, weeklyVerses: List<WeeklyVerse>, monthlyVerses: List<MonthlyVerse>) {
        importDailyVerses(dailyVerses)
        importWeeklyVerses(weeklyVerses)
        importMonthlyVerses(monthlyVerses)
    }

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