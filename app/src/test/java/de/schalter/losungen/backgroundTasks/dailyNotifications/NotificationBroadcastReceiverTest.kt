package de.schalter.losungen.backgroundTasks.dailyNotifications

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import de.schalter.losungen.dataAccess.Language
import de.schalter.losungen.dataAccess.daily.DailyVerse
import de.schalter.losungen.dataAccess.daily.DailyVersesDao
import de.schalter.losungen.utils.DatabaseUtils
import de.schalter.losungen.utils.Share
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowNotificationManager
import org.robolectric.shadows.ShadowToast
import java.util.*

@RunWith(RobolectricTestRunner::class)
class NotificationBroadcastReceiverTest {

    private lateinit var context: Context
    private lateinit var notificationManager: NotificationManager
    private lateinit var shadowNotificationManager: ShadowNotificationManager

    private lateinit var dailyVersesDao: DailyVersesDao
    private lateinit var dailyVerseLiveData: MutableLiveData<DailyVerse>
    private val dailyVerse = DailyVerse(
            date = Date(),
            oldTestamentVerseText = "oh yeah",
            oldTestamentVerseBible = "oh yeah2",
            newTestamentVerseText = "oh yeah3",
            newTestamentVerseBible = "oh yeah4",
            language = Language.DE
    )
    private val notificationContent = dailyVerse.oldTestamentVerseText + "\n" + dailyVerse.oldTestamentVerseBible +
            "\n\n" +
            dailyVerse.newTestamentVerseText + "\n" + dailyVerse.newTestamentVerseBible

    @Before
    fun setUp() {
        dailyVersesDao = DatabaseUtils.mockDailyVersesDao()
        dailyVerseLiveData = DatabaseUtils.mockDailyVerseDaoFindDailyVerseByDate()

        context = getApplicationContext()

        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        shadowNotificationManager = shadowOf(notificationManager)
    }

    private fun showNotification() {
        val intent = Intent(context, NotificationBroadcastReceiver::class.java)
        intent.action = NotificationBroadcastReceiver.ACTION_SHOW_NOTIFICATION
        context.sendBroadcast(intent)

        dailyVerseLiveData.postValue(dailyVerse)
    }

    @Test
    fun shouldShowNotification() {
        showNotification()

        assertEquals(1, shadowNotificationManager.activeNotifications.size)

        val notification = shadowNotificationManager.activeNotifications[0]
        val shadowNotification = shadowOf(notification.notification)
        assertEquals(
                notificationContent,
                shadowNotification.contentText)

        assertEquals(2, notification.notification.actions.size)
    }

    @Test
    fun shouldShareOnActionClick() {
        showNotification()

        val actions = shadowNotificationManager.activeNotifications[0].notification.actions
        val sharePendingIntent = actions[0].actionIntent
        val shareIntent = shadowOf(sharePendingIntent).savedIntent

        assertEquals(NotificationBroadcastReceiver.ACTION_SHARE, shareIntent.action)
        assertEquals(NotificationBroadcastReceiver::class.java.name, shareIntent.component?.className)

        mockkObject(Share)
        every { Share.text(any(), any(), any()) } returns Unit

        context.sendBroadcast(shareIntent)

        verify(exactly = 1) { Share.text(any(), notificationContent, any()) }
    }

    @Test
    fun shouldMarkOnActionClick() {
        showNotification()

        val actions = shadowNotificationManager.activeNotifications[0].notification.actions
        val markPendingIntent = actions[1].actionIntent
        val markIntent = shadowOf(markPendingIntent).savedIntent

        assertEquals(NotificationBroadcastReceiver.ACTION_MARK, markIntent.action)
        assertEquals(NotificationBroadcastReceiver::class.java.name, markIntent.component?.className)

        every { dailyVersesDao.updateIsFavourite(any(), any()) } returns Unit

        context.sendBroadcast(markIntent)

        verify(exactly = 1) {
            dailyVersesDao.updateIsFavourite(withArg {
                val dailyWordCalendar = Calendar.getInstance()
                dailyWordCalendar.time = dailyVerse.date

                val calendar = Calendar.getInstance()
                calendar.time = it
                calendar.set(Calendar.HOUR_OF_DAY, 12)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                assertEquals(dailyWordCalendar.time, calendar.time)
            }, true)
        }

        assertEquals(1, ShadowToast.shownToastCount())
    }
}