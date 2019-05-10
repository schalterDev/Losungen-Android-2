package schalter.de.losungen2.sermon

import io.reactivex.Observable
import schalter.de.losungen2.dataAccess.daily.DailyVerse
import schalter.de.losungen2.dataAccess.sermon.Sermon
import java.io.InputStream

abstract class SermonProvider {

    /**
     * Loads a sermon. That means that all data is present to download or
     * stream the sermon. Author and verse in bible is also present if possible
     * @return the download url of the sermon
     */
    abstract fun load(dailyVerse: DailyVerse): Observable<String>

    /**
     * Can only be called when the sermon was loaded. Saves the sermon to
     * the file system
     * @return absolute path of the sermon on the file system
     */
    abstract fun save(): Observable<String>

    /**
     * Can be called after save was successfull
     */
    abstract fun getSermon(): Sermon?

    /**
     * Can be called after load finished.
     * @return the verse the sermon is about when known.
     */
    abstract fun getVerseInBible(): String?

    /**
     * Can be called after load finished
     * @return the author of the sermon when known
     */
    abstract fun getAuthor(): String?

    /**
     * Loads the sermon and saves the sermon to the file system
     * @return absolute path of the sermon on the file system
     */
    fun loadAndSave(dailyVerse: DailyVerse): Observable<String> {
        return Observable.create<String> { emitter ->
            load(dailyVerse).blockingFirst()
            val path = save().blockingFirst()
            emitter.onNext(path)
        }
    }

    /**
     * Call this method to save the sermon to the file system like the users
     * spezified in the settings
     */
    protected fun saveSermon(input: InputStream) {
        // TODO implement
    }
}