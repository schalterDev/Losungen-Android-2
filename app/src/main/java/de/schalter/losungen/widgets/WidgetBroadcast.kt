package de.schalter.losungen.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import de.schalter.losungen.MainActivity
import de.schalter.losungen.R
import de.schalter.losungen.dataAccess.VersesDatabase
import de.schalter.losungen.dataAccess.daily.DailyVerse
import de.schalter.losungen.firebase.FirebaseUtil
import java.util.*

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
            val widgetData = WidgetData.load(context, widgetId)

            // Create an Intent to launch MainActivity
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            val views = RemoteViews(context.packageName, R.layout.app_widget)
            views.setOnClickPendingIntent(R.id.relLayout_widget, pendingIntent)

            views.setTextColor(R.id.textView_widget, widgetData.color)
            views.setInt(R.id.relLayout_widget, "setBackgroundColor", widgetData.background)
            views.setFloat(R.id.textView_widget, "setTextSize", widgetData.fontSize.toFloat())

            val dailyVerseLiveData = database.dailyVerseDao().findDailyVerseByDate(Date())
            val observer: androidx.lifecycle.Observer<DailyVerse> = object : androidx.lifecycle.Observer<DailyVerse> {
                override fun onChanged(dailyVerse: DailyVerse?) {
                    var textToShow: String? = null
                    dailyVerse?.let {
                        val oldTestamentText = dailyVerse.oldTestamentVerseText + "\n" + dailyVerse.oldTestamentVerseBible
                        val newTestamentText = dailyVerse.newTestamentVerseText + "\n" + dailyVerse.newTestamentVerseBible

                        textToShow = when (widgetData.content) {
                            WidgetContent.OLD_TESTAMENT -> oldTestamentText
                            WidgetContent.NEW_TESTAMENT -> newTestamentText
                            WidgetContent.OLD_AND_NEW_TESTAMENT -> oldTestamentText + "\n\n" + newTestamentText
                        }
                    }

                    if (textToShow == null) {
                        textToShow = context.getString(R.string.no_verses_found)
                    }
                    views.setTextViewText(R.id.textView_widget, textToShow)

                    // Tell the AppWidgetManager to perform an update on the current app widget
                    appWidgetManager.updateAppWidget(widgetId, views)
                    dailyVerseLiveData.removeObserver(this)
                }
            }
            dailyVerseLiveData.observeForever(observer)
        }
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle?) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)

        appWidgetManager.updateAppWidget(appWidgetId, RemoteViews(context.packageName, R.layout.app_widget))
    }
}