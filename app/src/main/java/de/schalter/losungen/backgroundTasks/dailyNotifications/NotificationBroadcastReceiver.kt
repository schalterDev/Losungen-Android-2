package de.schalter.losungen.backgroundTasks.dailyNotifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.core.app.NotificationCompat
import io.reactivex.Observable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import de.schalter.losungen.MainActivity
import de.schalter.losungen.R
import de.schalter.losungen.dataAccess.VersesDatabase
import de.schalter.losungen.dataAccess.daily.DailyVerse
import de.schalter.losungen.utils.PreferenceTags
import de.schalter.losungen.utils.Share
import java.util.*

class NotificationBroadcastReceiver : BroadcastReceiver() {

    private lateinit var context: Context

    override fun onReceive(context: Context, intent: Intent) {
        this.context = context

        when {
            intent.action == ACTION_SHOW_NOTIFICATION -> {
                createNotificationChannel()
                showNotification()
            }
            intent.action == ACTION_MARK -> {
                markNotification(intent.getLongExtra(EXTRA_DATE, Date().time))
            }
            intent.action == ACTION_SHARE -> {
                shareNotification(intent.getStringExtra(EXTRA_MESSAGE))
            }
        }
    }

    // ------------ INTENT SHOW -----------------

    @SuppressLint("CheckResult")
    private fun showNotification() {
        getNotificationData().firstElement().subscribe { notificationData ->
            val mBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                    .setContentTitle(notificationData.title)
                    .setStyle(NotificationCompat.BigTextStyle()
                            .bigText(notificationData.message))
                    .setContentText(notificationData.message)
                    .setAutoCancel(true)
                    .addAction(R.drawable.ic_action_share,
                            context.resources.getString(R.string.share),
                            getPendingActionShare(context, notificationData.message))
                    .addAction(R.drawable.ic_action_star,
                            context.resources.getString(R.string.mark_as_favorite),
                            getPendingActionMark(context, Date()))

            // intent when clicking on notification
            val openActivityIntent = PendingIntent.getActivity(context, 0,
                    Intent(context, MainActivity::class.java), 0)
            mBuilder.setContentIntent(openActivityIntent)

            val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, getNotificationChannelName(context), importance)
            channel.description = getNotificationChannelDescription(context)
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getPendingActionShare(context: Context, message: String): PendingIntent {
        val intent = Intent(context, NotificationBroadcastReceiver::class.java)
        intent.putExtra(EXTRA_MESSAGE, message)
        intent.action = ACTION_SHARE

        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getPendingActionMark(context: Context, date: Date): PendingIntent {
        val intent = Intent(context, NotificationBroadcastReceiver::class.java)
        intent.putExtra(EXTRA_DATE, date.time)
        intent.action = ACTION_MARK

        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getNotificationData(): Observable<NotificationData> {
        // create a observable that will be castet to a future
        return Observable.create<NotificationData> {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val notificationContent = preferences.getString(PreferenceTags.NOTIFICATION_CONTENT, CONTENT_OT_NT)

            val database = VersesDatabase.provideVerseDatabase(context)
            database.dailyVerseDao().findDailyVerseByDate(Date())
            val dailyVerseLiveData = database.dailyVerseDao().findDailyVerseByDate(Date())

            // get the data from livedata and push the result into the observable
            val observer: androidx.lifecycle.Observer<DailyVerse> = object : androidx.lifecycle.Observer<DailyVerse> {
                override fun onChanged(dailyVerse: DailyVerse?) {
                    var message: String? = null
                    dailyVerse?.let {
                        val oldTestamentText = dailyVerse.oldTestamentVerseText + "\n" + dailyVerse.oldTestamentVerseBible
                        val newTestamentText = dailyVerse.newTestamentVerseText + "\n" + dailyVerse.newTestamentVerseBible

                        message = when (notificationContent) {
                            CONTENT_OT -> oldTestamentText
                            CONTENT_NT -> newTestamentText
                            CONTENT_OT_NT -> oldTestamentText + "\n\n" + newTestamentText
                            else -> null
                        }
                    }

                    if (message == null) {
                        message = context.getString(R.string.no_verses_found)
                    }

                    it.onNext(
                            NotificationData(
                                    context.getString(R.string.daily_word),
                                    message!!))

                    dailyVerseLiveData.removeObserver(this)
                }
            }
            dailyVerseLiveData.observeForever(observer)
        }
    }

    // ---------- INTENT MARK OR SHARE ---------------
    private fun shareNotification(message: String) {
        val intent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        context.sendBroadcast(intent)

        Share.text(context, message)
    }

    private fun markNotification(date: Long) {
        val database = VersesDatabase.provideVerseDatabase(context)

        GlobalScope.launch {
            database.dailyVerseDao().updateIsFavourite(Date(date), true)
        }

        val intentClose = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        context.sendBroadcast(intentClose)

        Toast.makeText(context, context.resources.getString(R.string.marked_as_favourite), Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val ACTION_SHOW_NOTIFICATION = "dailyWord.DAILY_NOTIFICATION"
        const val ACTION_SHARE = "dailyWord.DAILY_NOTIFICATION_SHARE"
        const val ACTION_MARK = "dailyWord.DAILY_NOTIFICATION_MARK"

        const val EXTRA_DATE = "date"
        const val EXTRA_MESSAGE = "message"

        const val NOTIFICATION_CHANNEL_ID = "daily_word_notification"
        const val NOTIFICATION_ID = 241504

        private const val CONTENT_OT = "0"
        private const val CONTENT_NT = "1"
        private const val CONTENT_OT_NT = "2"

        fun getNotificationChannelName(context: Context): String {
            return context.getString(R.string.daily_word_notification_channel_name)
        }

        fun getNotificationChannelDescription(context: Context): String {
            return context.getString(R.string.daily_word_notification_channel_description)
        }
    }
}

private data class NotificationData(val title: String, val message: String)