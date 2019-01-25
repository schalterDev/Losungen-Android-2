package schalter.de.losungen2.components.dialogs.importDialog

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import schalter.de.losungen2.dataAccess.Language
import schalter.de.losungen2.dataAccess.VersesDatabase
import schalter.de.losungen2.dataAccess.availableData.AvailableData
import schalter.de.losungen2.utils.Constants
import java.util.*

const val DEBUG_TAG = "DataManagement"

class DataManagement(val context: Context) {

    data class YearLanguageUrl(
            var year: Int,
            var language: Language,
            var url: String,
            var copyrightUrl: String?
    ) {
        object Deserializer : ResponseDeserializable<Array<YearLanguageUrl>> {
            override fun deserialize(content: String): Array<YearLanguageUrl>? =
                    Gson().fromJson(content, Array<YearLanguageUrl>::class.java)
        }
    }

    private val versesDatabase = VersesDatabase.provideVerseDatabase(context)

    fun getAvailableData(): List<YearLanguageUrl> {
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

        return yearLanguageUrlList ?: listOf()
    }

    fun getImportedYears(language: Language): LiveData<List<AvailableData>> {
        return versesDatabase.availableDataDao().getAvailableDataForLanguage(language)
    }

    fun yearImported(year: Int, language: Language) {
        versesDatabase.availableDataDao().insertAvailableData(AvailableData(year, language))
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

        fun getUrlForYearAndLanguageFromData(
                data: List<YearLanguageUrl>,
                language: Language,
                year: Int): String? {
            return data.filter { dataElement -> dataElement.language == language && dataElement.year == year }.firstOrNull()?.url
        }
    }
}