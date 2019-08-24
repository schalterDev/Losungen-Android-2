package de.schalter.losungen.migration

import android.app.Activity
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import com.michaelflisar.changelog.ChangelogBuilder
import com.michaelflisar.changelog.classes.ChangelogFilter
import de.schalter.customize.Customize
import de.schalter.losungen.R
import de.schalter.losungen.backgroundTasks.dailyNotifications.ScheduleNotification
import de.schalter.losungen.dataAccess.VersesDatabase
import de.schalter.losungen.sermon.sermonProvider.SermonProvider
import de.schalter.losungen.utils.PreferenceTags
import java.io.File


/**
 * This class migrates the data from the old app (written in java).
 *
 * And also migrates data between versions of the new app
 */
class Migration(private val activity: Activity) {

    var progressChangeListener: OnProgressChanged? = null

    private val preference: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
    private val actualVersionCode: Int = activity.packageManager.getPackageInfo(activity.packageName, 0).versionCode
    private val versionCodeLastStart: Int

    init {
        versionCodeLastStart = preference.getInt(PreferenceTags.APP_VERSIONSCODE, actualVersionCode)
    }

    fun migrateIfNecessary() {
        if (actualVersionCode != versionCodeLastStart) {

            // show changelog
            if (activity is AppCompatActivity) {
                ChangelogBuilder()
                        .withSummary(true, true)
                        .apply {
                            // do not show changelog for sermon when language does not support sermon
                            if (!SermonProvider.implementationForLanguageAvailable(activity)) {
                                this.withFilter(ChangelogFilter(ChangelogFilter.Mode.NotContains, "sermon", true))
                            }
                        }
                        .buildAndShowDialog(activity, false)
            }
        }

        preference.edit().putInt(PreferenceTags.APP_VERSIONSCODE, actualVersionCode).apply()
    }

    fun migrateFromLegacyIfNecessary() {
        if (needMigrationFromLegacyApp()) {
            migrateFromLegacyApp()
        }

        preference.edit().putInt(PreferenceTags.APP_VERSIONSCODE, actualVersionCode).apply()
        progressChangeListener?.finished()
    }

    // --------- LEGACY MIGRATION -------
    fun needMigrationFromLegacyApp(): Boolean {
        return versionCodeLastStart <= HIGHEST_LEGACY_APP_VERSION_CODE
    }

    private fun migrateFromLegacyApp() {
        migrateDatabase()
        migrateSharedPreferences()
    }

    /**
     * load all old values, delete the old preferences and save them with the
     * new names. Schedule notification if it was set before
     */
    private fun migrateSharedPreferences() {
        progressChangeListener?.progressChanged(activity.getString(R.string.migrating_preferences))

        val autoDeleteAudio = preference.getString(LEGACY_PREF_DELETE_SERMONS_DAYS, "0")
        val showAds = preference.getBoolean(LEGACY_PREF_ADS, false)
        val sendStatistics = preference.getBoolean(LEGACY_PREF_ANALYTICS, false)
        val design = preference.getInt(LEGACY_DESIGN, 0)
        val notificationShow = preference.getBoolean(LEGACY_PREF_NOTIFICATION_SHOW, true)
        val notificationContent = preference.getString(LEGACY_PREF_NOTIFICATION_CONTENT, "2")
        var notificationTime: Long = 8 * 60
        try {
            notificationTime = preference.getLong(LEGACY_PREF_NOTIFICATION_TIME, 8 * 60)
        } catch (_: Exception) {
        }

        preference.edit()
                .clear()
                .putString(PreferenceTags.SERMONS_DELETE_AUTO, autoDeleteAudio)
                .putBoolean(PreferenceTags.SHOW_ADS, showAds)
                .putBoolean(PreferenceTags.SEND_STATISTICS, sendStatistics)
                .putInt(Customize.PREFERENCE_TAG, design)
                .putBoolean(PreferenceTags.NOTIFICATION_SHOW, notificationShow)
                .putString(PreferenceTags.NOTIFICATION_CONTENT, notificationContent)
                .putInt(PreferenceTags.NOTIFICATION_TIME, notificationTime.toInt())
                .putInt(PreferenceTags.APP_VERSIONSCODE, actualVersionCode)
                .putBoolean(PreferenceTags.FIRST_START, false)
                .apply()

        // schedule notification when is was set
        if (notificationShow) {
            ScheduleNotification(activity).scheduleNotification(instantlyShowNotification = true)
        }
    }

    /**
     * Has to run in a background thread
     */
    private fun migrateDatabase() {
        val language = preference.getString(LEGACY_LANGUAGE_VERSES, "de")!!

        val legacyDBHandler = LegacyDBHandler(activity)
        val versesDatabase = VersesDatabase.provideVerseDatabase(activity)

        progressChangeListener?.progressChanged(activity.getString(R.string.migrating_database))

        legacyDBHandler.getAllDailyWords().forEach {
            versesDatabase.dailyVerseDao().insertDailyVerse(it.toDailyVerse(language))
        }

        legacyDBHandler.getAllWeeklyVerses().forEach {
            versesDatabase.weeklyVerseDao().insertWeeklyVerse(it.toWeeklyVerse(language))
        }

        legacyDBHandler.getAllMonthlyVerses().forEach {
            versesDatabase.monthlyVerseDao().insertMonthlyVerse(it.toMonthlyVerse(language))
        }

        // delete all old sermons
        legacyDBHandler.getAllDailyWordsWithAudio().forEach {
            File(it.audioPath).delete()
        }

        // delete old databases
        legacyDBHandler.deleteDatabases(activity)
        activity.deleteDatabase("CustomLogger")
    }

    interface OnProgressChanged {
        fun progressChanged(step: String)
        fun finished()
    }

    companion object {
        private const val HIGHEST_LEGACY_APP_VERSION_CODE = 99

        // LEGACY PREF TAGS
        private const val LEGACY_PREF_NOTIFICATION_SHOW = "notifications_losung" // Boolean
        private const val LEGACY_PREF_NOTIFICATION_CONTENT = "notifications_art" // String 0, 1, 2
        private const val LEGACY_PREF_NOTIFICATION_TIME = "notification_time" // Long hour * 60 + minutes

        private const val LEGACY_PREF_DELETE_SERMONS_DAYS = "audio_delete_days" // String

        private const val LEGACY_PREF_ANALYTICS = "google_analytics" // Boolean
        private const val LEGACY_PREF_ADS = "show_ads" // Boolean

        private const val LEGACY_DESIGN = "color_custom_design" // Int, 0: Blue, 1: Yellow, 2: Green, 3: Red, 4: Dark, 5: Light

        private const val LEGACY_LANGUAGE_VERSES = "selected_language"
    }
}