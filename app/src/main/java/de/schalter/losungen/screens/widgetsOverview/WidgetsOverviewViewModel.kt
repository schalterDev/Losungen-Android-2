package de.schalter.losungen.screens.widgetsOverview

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import de.schalter.losungen.dataAccess.VersesDatabase
import de.schalter.losungen.dataAccess.daily.DailyVerse
import de.schalter.losungen.widgets.WidgetContent
import de.schalter.losungen.widgets.WidgetContentType
import de.schalter.losungen.widgets.WidgetData
import java.util.*

class WidgetsOverviewViewModel(private val mApplication: Application, private val database: VersesDatabase) : AndroidViewModel(mApplication) {

    private var mediatorLiveDataWidgetData: MediatorLiveData<List<WidgetData>>? = null
    private var dailyVerse: DailyVerse? = null
    private var widgetData: MutableList<WidgetData> = mutableListOf()

    fun loadWidgetData() {
        widgetData = WidgetData.loadAll(mApplication).toMutableList()
        widgetData.forEach { it.content = getWidgetContentToShow(it.contentType) }

        mediatorLiveDataWidgetData?.postValue(widgetData)
    }

    fun getAllWidgets(): LiveData<List<WidgetData>> {
        mediatorLiveDataWidgetData = MediatorLiveData<List<WidgetData>>().apply {
            this.addSource(database.dailyVerseDao().findDailyVerseByDate(Calendar.getInstance().time)) { verse ->
                dailyVerse = verse
                loadWidgetData()
            }
        }

        return mediatorLiveDataWidgetData!!
    }

    private fun getWidgetContentToShow(widgetContentType: Set<WidgetContentType>): MutableList<WidgetContent> {
        val widgetContent = mutableListOf<WidgetContent>()

        dailyVerse?.let { dailyVerse ->
            if (widgetContentType.contains(WidgetContentType.OLD_TESTAMENT)) {
                widgetContent.add(WidgetContent(dailyVerse.oldTestamentVerseText, dailyVerse.oldTestamentVerseBible))
            }
            if (widgetContentType.contains(WidgetContentType.NEW_TESTAMENT)) {
                widgetContent.add(WidgetContent(dailyVerse.newTestamentVerseText, dailyVerse.newTestamentVerseBible))
            }
        }

        return widgetContent
    }
}

class WidgetsOverviewViewModelFactory(private val application: Application, private val context: Context) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WidgetsOverviewViewModel(application, VersesDatabase.provideVerseDatabase(context)) as T
    }
}
