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
    private var newTestamentText: String? = null

    init {
        widgetDataLiveData = MediatorLiveData<WidgetData>().apply {
            addSource(database.dailyVerseDao().findDailyVerseByDate(date)) {
                oldTestamentText = it.oldTestamentVerseText + "\n" + it.oldTestamentVerseBible
                newTestamentText = it.newTestamentVerseText + "\n" + it.newTestamentVerseBible

                updateWidgetData(content = widgetData?.content)
            }
        }
    }

    fun setWidgetData(widgetData: WidgetData) {
        this.widgetData = widgetData
        updateWidgetData(content = widgetData.content)
    }

    fun updateWidgetData(color: Int? = null, background: Int? = null, fontSize: Int? = null, content: WidgetContent? = null) {
        widgetData?.let { widgetData ->
            color?.let { widgetData.color = it }
            background?.let { widgetData.background = it }
            fontSize?.let { widgetData.fontSize = it }
            content?.let {
                if (newTestamentText != null && oldTestamentText != null) {
                    widgetData.content = it
                    widgetData.contentToShow = when (it) {
                        WidgetContent.OLD_TESTAMENT -> oldTestamentText
                        WidgetContent.NEW_TESTAMENT -> newTestamentText
                        WidgetContent.OLD_AND_NEW_TESTAMENT -> oldTestamentText + "\n\n" + newTestamentText
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