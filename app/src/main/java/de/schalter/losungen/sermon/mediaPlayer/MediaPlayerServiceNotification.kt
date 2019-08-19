package de.schalter.losungen.sermon.mediaPlayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import de.schalter.losungen.MainActivity
import de.schalter.losungen.R

class MediaPlayerServiceNotification(
        private val context: Context,
        private val mediaPlayerService: MediaPlayerService,
        private var title: String,
        var bigIcon: Int = R.mipmap.ic_launcher,
        var smallIcon: Int = R.drawable.ic_play_arrow,
        var playIcon: Int = R.drawable.ic_play_arrow,
        var pauseIcon: Int = R.drawable.ic_pause,
        var stopIcon: Int = R.drawable.ic_close) {

    private val mBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
    private val mContentView = RemoteViews(context.packageName, R.layout.view_media_player_notification)

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

    fun getNotification(): Notification {
        mBuilder.setSmallIcon(smallIcon)

        mContentView.setTextViewText(R.id.audio_notification_title, title)
        mContentView.setImageViewResource(R.id.image_view_logo, bigIcon)
        mContentView.setImageViewResource(R.id.audio_notification_play, getPlayIconResource())
        mContentView.setImageViewResource(R.id.audio_notification_close, stopIcon)

        mContentView.setOnClickPendingIntent(R.id.audio_notification_play, getPendingIntentPlayIcon())
        mContentView.setOnClickPendingIntent(R.id.audio_notification_close, getPendingIntentCloseIcon())

        mBuilder.setContent(mContentView)

        mBuilder.setContentIntent(getPendingIntentNotification())

        return mBuilder.build()
    }

    private fun getPlayIconResource(): Int {
        return if (mediaPlayerService.getState() == MediaPlayerService.State.Playing) {
            pauseIcon
        } else {
            playIcon
        }
    }

    private fun getPendingIntentPlayIcon(): PendingIntent {
        val intent = Intent(context, MediaPlayerService::class.java)
        intent.action =
                if (mediaPlayerService.getState() == MediaPlayerService.State.Playing)
                    MediaPlayerService.ACTION_PAUSE
                else
                    MediaPlayerService.ACTION_RESUME

        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getPendingIntentCloseIcon(): PendingIntent {
        val intent = Intent(context, MediaPlayerService::class.java)
        intent.action = MediaPlayerService.ACTION_STOP

        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getPendingIntentNotification(): PendingIntent {
        return pendingIntentNotification
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "media-player-notification-id"
        var notificationChannelIdRes = R.string.media_player_notification_channel_id
        var notificationChannelDescriptionRes = R.string.media_player_notification_channel_description
        const val NOTIFICATION_ID = 8645156
    }
}