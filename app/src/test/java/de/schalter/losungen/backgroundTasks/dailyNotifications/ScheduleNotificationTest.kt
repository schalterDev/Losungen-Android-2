package de.schalter.losungen.backgroundTasks.dailyNotifications

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowAlarmManager
import java.util.*


@RunWith(RobolectricTestRunner::class)
class ScheduleNotificationTest {

    private lateinit var context: Context
    private lateinit var alarmManager: AlarmManager
    private lateinit var shadowAlarmManager: ShadowAlarmManager

    private lateinit var scheduleNotification: ScheduleNotification
    private lateinit var now: Calendar

    @Before
    fun setUp() {
        context = getApplicationContext()

        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        shadowAlarmManager = shadowOf(alarmManager)

        scheduleNotification = ScheduleNotification(context)
        now = Calendar.getInstance()
    }

    @Test
    fun shouldScheduleAndCancelNotification() {
        assertNull(shadowAlarmManager.nextScheduledAlarm)

        scheduleNotification.scheduleNotification()
        assertNotNull(shadowAlarmManager.nextScheduledAlarm)

        scheduleNotification.cancelScheduledNotification()
        assertNull(shadowAlarmManager.nextScheduledAlarm)
    }

    @Test
    fun shouldScheduleForRightTime() {
        val hour = 5
        val minute = 20

        assertNull(shadowAlarmManager.nextScheduledAlarm)

        scheduleNotification.scheduleNotification(hour * 60 + minute)

        val nextAlarm = shadowAlarmManager.nextScheduledAlarm
        assertNotNull(nextAlarm)

        assertEquals(AlarmManager.INTERVAL_DAY, nextAlarm.interval)

        val calendar = Calendar.getInstance()
        calendar.time = Date(nextAlarm.triggerAtTime)
        assertEquals(hour, calendar.get(Calendar.HOUR))
        assertEquals(minute, calendar.get(Calendar.MINUTE))
    }

    fun shouldScheduleForTomorrow() {
        assertNull(shadowAlarmManager.nextScheduledAlarm)

        scheduleNotification.scheduleNotification(0)

        val nextAlarm = shadowAlarmManager.nextScheduledAlarm
        assertNotNull(nextAlarm)

        assertEquals(AlarmManager.INTERVAL_DAY, nextAlarm.interval)

        val calendar = Calendar.getInstance()
        calendar.time = Date(nextAlarm.triggerAtTime)
        now.add(Calendar.DAY_OF_YEAR, 1)
        assertEquals(now, calendar.get(Calendar.DAY_OF_YEAR))
    }

    fun shouldScheduleForToday() {
        assertNull(shadowAlarmManager.nextScheduledAlarm)

        scheduleNotification.scheduleNotification(23 * 60 + 59)

        val nextAlarm = shadowAlarmManager.nextScheduledAlarm
        assertNotNull(nextAlarm)

        assertEquals(AlarmManager.INTERVAL_DAY, nextAlarm.interval)

        val calendar = Calendar.getInstance()
        calendar.time = Date(nextAlarm.triggerAtTime)
        assertEquals(now, calendar.get(Calendar.DAY_OF_YEAR))
    }

    fun shouldShowNotificationInstant() {
        scheduleNotification.scheduleNotification(instantlyShowNotification = true)

        val intents = shadowOf(getApplicationContext<Application>()).broadcastIntents
        assertEquals(1, intents.size)
        assertEquals(NotificationBroadcastReceiver.ACTION_SHOW_NOTIFICATION, intents[0].action)
    }

    fun shouldNotShowNotificationInstant() {
        scheduleNotification.scheduleNotification(instantlyShowNotification = false)

        val intents = shadowOf(getApplicationContext<Application>()).broadcastIntents
        assertEquals(0, intents.size)
    }

}