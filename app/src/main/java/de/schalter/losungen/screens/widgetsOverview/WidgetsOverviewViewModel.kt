package de.schalter.losungen.screens.widgetsOverview

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import de.schalter.losungen.dataAccess.VersesDatabase
import de.schalter.losungen.dataAccess.daily.DailyVerse
import de.schalter.losungen.widgets.WidgetContent
import de.schalter.losungen.widgets.WidgetData
import java.util.*

class WidgetsOverviewViewModel(private val mApplication: Application, private val database: VersesDatabase) : AndroidViewModel(mApplication) {

    private var mediatorLiveDataWidgetData: MediatorLiveData<List<WidgetData>>? = null
    private var dailyVerse: DailyVerse? = null
    private var widgetData: MutableList<WidgetData> = mutableListOf()

    fun loadWidgetData() {
        widgetData = WidgetData.loadAll(mApplication).toMutableList()
        widgetData.forEach { it.contentToShow = getWidgetContentToShow(it.content) }

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

    private fun getWidgetContentToShow(widgetContent: WidgetContent): String? {
        dailyVerse?.let { dailyVerse ->
            val oldTestamentText = dailyVerse.oldTestamentVerseText + "\n" + dailyVerse.oldTestamentVerseBible
            val newTestamentText = dailyVerse.newTestamentVerseText + "\n" + dailyVerse.newTestamentVerseBible

            return when (widgetContent) {
                WidgetContent.OLD_TESTAMENT -> oldTestamentText
                WidgetContent.NEW_TESTAMENT -> newTestamentText
                WidgetContent.OLD_AND_NEW_TESTAMENT -> oldTestamentText + "\n\n" + newTestamentText
            }
        }
        return null
    }
}

class WidgetsOverviewViewModelFactory(private val application: Application, private val context: Context) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WidgetsOverviewViewModel(application, VersesDatabase.provideVerseDatabase(context)) as T
    }
}
