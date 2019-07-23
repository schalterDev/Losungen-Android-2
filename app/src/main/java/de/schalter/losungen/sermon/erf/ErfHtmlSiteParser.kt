package de.schalter.losungen.sermon.erf

import io.reactivex.Single
import java.net.URL

private const val AUDIO_URL_PREFIX = "https://www.erf.de"
private const val HTML_BEGIN = "data-file=\""
private const val HTML_END = "\""

class ErfHtmlSiteParser(private val url: String) {

    fun getDownloadPathMp3(): Single<String> {
        return Single.fromCallable {
            val html = downloadHtml()
            val indexBegin = html.indexOf(HTML_BEGIN) + HTML_BEGIN.length
            val indexEnd = html.indexOf(HTML_END, indexBegin)

            val mp3Url = AUDIO_URL_PREFIX + html.substring(indexBegin, indexEnd)
            mp3Url
        }
    }

    private fun downloadHtml(): String {
        return URL(url).readText()
    }

}