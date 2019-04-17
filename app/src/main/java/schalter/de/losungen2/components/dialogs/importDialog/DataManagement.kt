package schalter.de.losungen2.components.dialogs.importDialog

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import schalter.de.losungen2.dataAccess.Language
import schalter.de.losungen2.utils.Constants
import schalter.de.losungen2.utils.CoroutineDispatchers
import java.util.*
import kotlin.coroutines.CoroutineContext

const val DEBUG_TAG = "DataManagement"

class DataManagement : ViewModel(), CoroutineScope {

    data class YearLanguageUrl(
            var year: Int,
            var language: Language,
            var url: String,
            var copyrightUrl: String? = null,
            var fileNameDailyVerses: String? = null,
            var fileNameMonthlyVerses: String? = null,
            var fileNameWeeklyVerses: String? = null
    ) {
        object Deserializer : ResponseDeserializable<Array<YearLanguageUrl>> {
            override fun deserialize(content: String): Array<YearLanguageUrl>? =
                    Gson().fromJson(content, Array<YearLanguageUrl>::class.java)
        }
    }

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = CoroutineDispatchers.Background + job

    fun getAvailableData(): LiveData<List<YearLanguageUrl>> {
        val liveData = MutableLiveData<List<YearLanguageUrl>>()

        launch {
            val countUrls = Constants.urlsOverviewAvailableData.size
            var indexUrl = 0

            var yearLanguageUrlList: List<YearLanguageUrl>? = null

            // try all urls when the one before fails
            do {
                val url = Constants.urlsOverviewAvailableData[indexUrl]
                indexUrl++

                val (_: Request, response: Response, result) = Fuel.get(url).responseObject(YearLanguageUrl.Deserializer)
                val (yearLanguageUrlArray: Array<YearLanguageUrl>?, error) = result

                if (yearLanguageUrlArray != null && response.statusCode == 200 && error == null) {
                    yearLanguageUrlList = Arrays.asList(*yearLanguageUrlArray)
                } else {
                    Log.e(DEBUG_TAG, "Error while trying to load available data")
                    Log.e(DEBUG_TAG, "Response: $response")
                    Log.e(DEBUG_TAG, "Error: $error")
                }
            } while (indexUrl < countUrls && yearLanguageUrlList == null)

            liveData.postValue(yearLanguageUrlList ?: listOf())
        }

        return liveData
    }

    companion object {
        fun getLanguagesFromData(data: List<YearLanguageUrl>): List<String> {
            return data.map { dataElement -> dataElement.language.toLongString() }.distinct()
        }

        fun getYearsForLanguageFromData(data: List<YearLanguageUrl>, language: Language): List<Int> {
            return data.filter { dataElement -> dataElement.language == language }
                    .map { dataElement -> dataElement.year }
                    .distinct()
        }
    }
}