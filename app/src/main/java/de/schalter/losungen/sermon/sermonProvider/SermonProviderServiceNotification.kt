package de.schalter.losungen.sermon.sermonProvider

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import de.schalter.losungen.MainActivity
import de.schalter.losungen.R

class SermonProviderServiceNotification(
        private val context: Context,
        private val sermonProviderService: SermonProviderService,
        private val smallIcon: Int = R.drawable.ic_download) {

    private val mBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)

    private val pendingIntentNotification: PendingIntent

    init {
        createNotificationChannel(context)
        mBuilder.setAutoCancel(false)

        val intent = Intent(context, MainActivity::class.java)
        pendingIntentNotification = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, context.getString(notificationChannelIdRes), importance)
            channel.description = context.getString(notificationChannelDescriptionRes)
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun getNotification(state: State, content: String? = null): Notification {
        mBuilder.setSmallIcon(smallIcon)

        when (state) {
            State.SearchingSermon ->
                mBuilder.setContentTitle(context.getString(R.string.searching_sermon))
            State.DownloadingSermon ->
                mBuilder.setContentTitle(context.getString(R.string.downloading_sermon))
            State.Error -> {
                mBuilder.setContentTitle(context.getString(R.string.error_downloading_sermon))
            }
        }

        content?.let { mBuilder.setContentText(it) }

        mBuilder.setContentIntent(getPendingIntentNotification())

        return mBuilder.build()
    }

    private fun getPendingIntentNotification(): PendingIntent {
        return pendingIntentNotification
    }

    enum class State {
        SearchingSermon,
        DownloadingSermon,
        Error
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "sermon-provider-notification-id"
        var notificationChannelIdRes = R.string.sermon_provider_notification_channel_id
        var notificationChannelDescriptionRes = R.string.sermon_provider_notification_channel_description
        const val NOTIFICATION_ID = 1357468
    }
}