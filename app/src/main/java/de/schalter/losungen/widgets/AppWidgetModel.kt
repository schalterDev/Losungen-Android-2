package de.schalter.losungen.widgets

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.schalter.losungen.dataAccess.VersesDatabase
import java.util.*

class AppWidgetModel(private var database: VersesDatabase, private var date: Date) : ViewModel() {
    private var widgetData: WidgetData? = null
    private var widgetDataLiveData: MediatorLiveData<WidgetData>

    private var oldTestamentText: String? = null
    private var oldTestamentVerse: String? = null
    private var newTestamentText: String? = null
    private var newTestamentVerse: String? = null

    init {
        widgetDataLiveData = MediatorLiveData<WidgetData>().apply {
            addSource(database.dailyVerseDao().findDailyVerseByDate(date)) {
                oldTestamentText = it?.oldTestamentVerseText
                oldTestamentVerse = it?.oldTestamentVerseBible
                newTestamentText = it?.newTestamentVerseText
                newTestamentVerse = it?.newTestamentVerseBible

                updateWidgetData(contentType = widgetData?.contentType)
            }
        }
    }

    fun setWidgetData(widgetData: WidgetData) {
        this.widgetData = widgetData
        updateWidgetData(contentType = widgetData.contentType)
    }

    fun updateWidgetData(color: Int? = null, background: Int? = null, fontSize: Int? = null, contentType: Set<WidgetContentType>? = null) {
        widgetData?.let { widgetData ->
            color?.let { widgetData.color = it }
            background?.let { widgetData.background = it }
            fontSize?.let { widgetData.fontSize = it }
            contentType?.let { contentType ->
                if (newTestamentText != null && oldTestamentText != null) {
                    widgetData.contentType = contentType
                    widgetData.content = mutableListOf()

                    if (contentType.contains(WidgetContentType.OLD_TESTAMENT)) {
                        widgetData.content.add(WidgetContent(oldTestamentText!!, oldTestamentVerse!!))
                    }
                    if (contentType.contains(WidgetContentType.NEW_TESTAMENT)) {
                        widgetData.content.add(WidgetContent(newTestamentText!!, newTestamentVerse!!))
                    }
                }
            }

            widgetDataLiveData.postValue(widgetData)
        }
    }

    fun getWidgetData(): LiveData<WidgetData> {
        return widgetDataLiveData
    }
}

class AppWidgetModelFactory(private val context: Context, private val date: Date) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AppWidgetModel(VersesDatabase.provideVerseDatabase(context), date) as T
    }

}