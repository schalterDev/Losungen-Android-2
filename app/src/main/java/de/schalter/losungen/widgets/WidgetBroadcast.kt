package de.schalter.losungen.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.RemoteViews
import de.schalter.losungen.MainActivity
import de.schalter.losungen.R
import de.schalter.losungen.dataAccess.VersesDatabase
import de.schalter.losungen.firebase.FirebaseUtil

class WidgetBroadcast : AppWidgetProvider() {

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            WidgetData.remove(context, appWidgetId)
        }

        FirebaseUtil.trackWidgetDeleted(context)

        super.onDeleted(context, appWidgetIds)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val database = VersesDatabase.provideVerseDatabase(context)

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (widgetId in appWidgetIds) {
            val startServiceIntent = Intent(context, AppWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
            }

            val views = RemoteViews(context.packageName, R.layout.app_widget).apply {
                val widgetData = WidgetData.load(context, widgetId)

                this.setInt(R.id.relLayout_widget, "setBackgroundColor", widgetData.background)
                setRemoteAdapter(R.id.listView_widget, startServiceIntent)

                // Create an Intent to launch MainActivity
                val startActivityIntent = Intent(context, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(context, 0, startActivityIntent, 0)
                setPendingIntentTemplate(R.id.listView_widget, pendingIntent)
            }

            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle?) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)

        appWidgetManager.updateAppWidget(appWidgetId, RemoteViews(context.packageName, R.layout.app_widget))
    }
}