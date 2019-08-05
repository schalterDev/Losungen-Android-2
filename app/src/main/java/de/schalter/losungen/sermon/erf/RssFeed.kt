package de.schalter.losungen.sermon.erf

import com.prof.rssparser.Parser
import de.schalter.losungen.R
import de.schalter.losungen.components.exceptions.TranslatableException
import de.schalter.losungen.dataAccess.daily.DailyVerse
import io.reactivex.Single
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
                        // foundArticle.author is null. But the author is in the description
                        // Example: Some text (Autor: ...)
                        val authorStartIndex = foundArticle.description?.indexOf(AUTHOR_START_TEXT)
                                ?: -1
                        val authorEndIndex = foundArticle.description?.indexOf(AUTHOR_END_TEXT, authorStartIndex)
                                ?: -1
                        val author = if (authorStartIndex != -1 && authorEndIndex != -1) {
                            foundArticle.description?.substring(authorStartIndex + AUTHOR_START_TEXT.length, authorEndIndex)?.trim()
                        } else {
                            null
                        }

                        emitter.onSuccess(FeedLoaded(foundArticle.link!!, author = author, bibleVerse = foundArticle.title))
                    }
                } catch (e: Exception) {
                    emitter.onError(TranslatableException(R.string.could_not_get_sermon_list))
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

        const val AUTHOR_START_TEXT = "Autor:"
        const val AUTHOR_END_TEXT = ")"
    }
}