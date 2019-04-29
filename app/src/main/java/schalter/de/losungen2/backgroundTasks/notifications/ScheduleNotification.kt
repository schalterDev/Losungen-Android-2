package schalter.de.losungen2.backgroundTasks.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.preference.PreferenceManager
import schalter.de.losungen2.utils.PreferenceTags
import java.util.*


/**
 * Schedule notification to show the daily word every day
 */
class ScheduleNotification(private val context: Context) {

    /**
     * Schedules a notification and delete all schedules before
     */
    private fun scheduleNotification(hour: Int, minute: Int) {
        cancelScheduledNotification(false)

        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)

        // Do not show notification for today when time passed
        if (calendar.time.before(Calendar.getInstance().time)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY, getPendingIntent())

        enableBootBroadcast()
    }

    /**
     * Renew alarm manager after reboot
     */
    fun scheduleNotification(time: Int? = null, instantlyShowNotification: Boolean = false) {
        var notificationTime = time

        if (notificationTime == null) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            notificationTime = preferences.getInt(PreferenceTags.NOTIFICATION_TIME, PreferenceTags.NOTIFICATION_TIME_DEFAULT_VALUE)
        }
        val minute = notificationTime % 60
        val hour = notificationTime / 60
        scheduleNotification(hour, minute)

        if (instantlyShowNotification) {
            context.sendBroadcast(Intent(context, NotificationBroadcastReceiver::class.java).apply { action = NotificationBroadcastReceiver.ACTION })
        }
    }

    /**
     * Cancel scheduled notification
     */
    fun cancelScheduledNotification(disableBootBroadcast: Boolean = true) {
        if (disableBootBroadcast) {
            disableBootBroadcast()
        }

        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        manager!!.cancel(getPendingIntent())
    }

    private fun getPendingIntent(): PendingIntent {
        val notificationIntent = Intent(context, NotificationBroadcastReceiver::class.java)
        notificationIntent.action = NotificationBroadcastReceiver.ACTION
        return PendingIntent.getBroadcast(context, 0, notificationIntent, 0)
    }

    private fun enableBootBroadcast() {
        val packageManager = context.packageManager
        val componentName = ComponentName(context, DeviceBootReceiver::class.java)
        packageManager.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP)
    }

    private fun disableBootBroadcast() {
        val packageManager = context.packageManager
        val componentName = ComponentName(context, DeviceBootReceiver::class.java)
        packageManager.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP)
    }

}