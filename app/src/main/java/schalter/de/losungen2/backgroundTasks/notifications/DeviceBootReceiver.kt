package schalter.de.losungen2.backgroundTasks.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class DeviceBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            // recreate the alarm for showing notification
            ScheduleNotification(context).scheduleNotification()
        }
    }
}