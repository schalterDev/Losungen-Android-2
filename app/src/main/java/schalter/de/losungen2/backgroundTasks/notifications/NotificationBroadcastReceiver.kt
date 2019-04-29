package schalter.de.losungen2.backgroundTasks.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class NotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION) {
            Toast.makeText(context, "Test Notification", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        const val ACTION = "dailyWord.DAILY_NOTIFICATION"
    }
}