package de.schalter.losungen.sermon.erf

import de.schalter.losungen.R
import de.schalter.losungen.components.exceptions.TranslatableException
import io.reactivex.Single
import java.net.URL

private const val AUDIO_URL_PREFIX = "https://www.erf.de"
private const val HTML_BEGIN = "data-file=\""
private const val HTML_END = "\""

class ErfHtmlSiteParser(private val url: String) {

    fun getDownloadPathMp3(): Single<String> {
        return Single.create {
            val html: String
            try {
                html = downloadHtml()
            } catch (e: Exception) {
                it.onError(TranslatableException(R.string.error_downloading_site_of_sermon))
                return@create
            }
            val indexBegin = html.indexOf(HTML_BEGIN) + HTML_BEGIN.length
            val indexEnd = html.indexOf(HTML_END, indexBegin)

            if (indexBegin == -1 || indexEnd == -1) {
                it.onError(TranslatableException(R.string.no_mp3_found))
                return@create
            }

            val mp3Url = AUDIO_URL_PREFIX + html.substring(indexBegin, indexEnd)
            it.onSuccess(mp3Url)
        }
    }

    private fun downloadHtml(): String {
        return URL(url).readText()
    }

}