package schalter.de.losungen2.sermon.erf

import android.content.Context
import io.reactivex.Single
import schalter.de.losungen2.dataAccess.daily.DailyVerse
import schalter.de.losungen2.dataAccess.sermon.Sermon
import schalter.de.losungen2.sermon.SermonProvider
import java.io.BufferedInputStream
import java.net.URL

class ErfWortZumTagSermonImplementation(context: Context) : SermonProvider(context) {

    private var dailyVerse: DailyVerse? = null

    private var author: String? = null
    private var downloadPathMp3: String? = null
    private var savePathMp3: String? = null

    override fun loadDownloadUrl(dailyVerse: DailyVerse): Single<String?> {
        this.dailyVerse = dailyVerse

        return RssFeed().load(URL_RSS_FEED, dailyVerse.date).map { feedData ->
            author = feedData.author
            feedData.downloadPath
        }.flatMap { htmlDownloadPath ->
            ErfHtmlSiteParser(htmlDownloadPath).getDownloadPathMp3()
        }.map {
            downloadPathMp3 = it
            downloadPathMp3
        }
    }

    override fun getFileName() = "erf-${downloadPathMp3!!.substring(downloadPathMp3!!.lastIndexOf("/") + 1)}"

    override fun save(): Single<String?> {
        return Single.fromCallable {
            val inputStream = BufferedInputStream(URL(downloadPathMp3).openStream())
            savePathMp3 = saveSermon(inputStream)
            inputStream.close()
            saveToDatabase()
            savePathMp3
        }
    }

    override fun getSermon(): Sermon? {
        return Sermon(
                dailyVerseId = this.dailyVerse!!.dailyVerseId!!,
                downloadUrl = downloadPathMp3!!,
                pathSaved = savePathMp3!!,
                author = author,
                provider = getProviderName())
    }

    override fun getVerseInBible(): String? {
        // TODO get verse in bible for erf sermons
        return null
    }

    override fun getAuthor() = author

    override fun getProviderName(): String = "ERF Wort zum Tag"

    companion object {
        const val URL_RSS_FEED = "https://feedpress.me/erf-plus-wortzumtag"
    }
}