package schalter.de.losungen2.sermon.erf

import com.prof.rssparser.Parser
import io.reactivex.Single
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import schalter.de.losungen2.R
import schalter.de.losungen2.components.exceptions.TranslatableException
import schalter.de.losungen2.dataAccess.daily.DailyVerse
import java.text.SimpleDateFormat
import java.util.*

class RssFeed {
    fun load(url: String, date: Date): Single<FeedLoaded> {
        return Single.create { emitter ->
            GlobalScope.launch {
                try {
                    val articleList = Parser().getArticles(url)
                    val foundArticle = articleList.find { article ->
                        if (article.pubDate != null) {
                            try {
                                sameDate(date, article.pubDate!!)
                            } catch (_: NumberFormatException) {
                                emitter.onError(TranslatableException(R.string.could_not_parse_date_of_rss))
                                false
                            }
                        } else {
                            false
                        }
                    }

                    if (foundArticle?.link == null) {
                        emitter.onError(TranslatableException(R.string.no_sermon_found))
                    } else {
                        emitter.onSuccess(FeedLoaded(foundArticle.author, foundArticle.link!!))
                    }
                } catch (e: Exception) {
                    emitter.onError(e)
                }
            }
        }
    }

    private fun sameDate(dailyWordDate: Date, articleDate: String): Boolean {
        val simpleDateFormat = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)
        val parsedDate = simpleDateFormat.parse(articleDate)

        return DailyVerse.getDateForDay(dailyWordDate).time == DailyVerse.getDateForDay(parsedDate).time
    }

    companion object {
        const val DATE_FORMAT = "EEE, d MMM yyyy hh:mm:ss Z"
    }
}