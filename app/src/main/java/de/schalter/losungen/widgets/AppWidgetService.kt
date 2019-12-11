package de.schalter.losungen.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import de.schalter.losungen.R
import de.schalter.losungen.dataAccess.VersesDatabase
import de.schalter.losungen.dataAccess.daily.DailyVerse
import de.schalter.losungen.utils.CoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext


class AppWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return AppWidgetRemoteViewsFactory(this.applicationContext, intent)
    }
}

class AppWidgetRemoteViewsFactory(
        private val context: Context,
        intent: Intent
) : RemoteViewsService.RemoteViewsFactory, CoroutineScope {

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = CoroutineDispatchers.Background + job

    private val appWidgetId: Int = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID)

    private val database = VersesDatabase.provideVerseDatabase(context)
    private lateinit var widgetData: WidgetData
    private var dailyVerse: DailyVerse? = null

    override fun onCreate() {
        widgetData = WidgetData.load(context, appWidgetId)
        launch {
            dailyVerse = database.dailyVerseDao().findDailyVerseByDateSynchronous(Calendar.getInstance().time)
        }
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onDataSetChanged() {}

    override fun hasStableIds(): Boolean = true

    override fun getViewAt(position: Int): RemoteViews? {
        // when false show verse from new testament
        val showVerseFromOldTestament = if (position > 0) {
            false
        } else {
            when (widgetData.content) {
                WidgetContent.OLD_TESTAMENT -> true
                WidgetContent.NEW_TESTAMENT -> false
                WidgetContent.OLD_AND_NEW_TESTAMENT -> true
            }
        }

        var text = context.getString(R.string.no_verses_found)
        var verse = ""
        dailyVerse?.let { dailyVerse ->
            text = if (showVerseFromOldTestament) {
                dailyVerse.oldTestamentVerseText
            } else {
                dailyVerse.newTestamentVerseText
            }

            verse = if (showVerseFromOldTestament) {
                dailyVerse.oldTestamentVerseBible
            } else {
                dailyVerse.newTestamentVerseBible
            }
        }

        val views = RemoteViews(context.packageName, R.layout.app_widget_row)
        views.setTextViewText(R.id.textView_widget, text)
        views.setTextViewText(R.id.textViewVerse_widget, verse)
        views.setTextColor(R.id.textView_widget, widgetData.color)
        views.setTextColor(R.id.textViewVerse_widget, widgetData.color)
        views.setFloat(R.id.textView_widget, "setTextSize", widgetData.fontSize.toFloat())
        views.setFloat(R.id.textViewVerse_widget, "setTextSize", widgetData.fontSize.toFloat() - 2)

        // delegate click listener to WidgetBroadcast
        views.setOnClickFillInIntent(R.id.linearLayout_widgetRow, Intent())

        return views
    }

    override fun getCount(): Int {
        return when (widgetData.content) {
            WidgetContent.NEW_TESTAMENT -> 1
            WidgetContent.OLD_TESTAMENT -> 1
            WidgetContent.OLD_AND_NEW_TESTAMENT -> 2
        }
    }

    override fun getViewTypeCount(): Int = 1
    override fun onDestroy() {
        job.cancel()
    }

    override fun getLoadingView(): RemoteViews? = null

}