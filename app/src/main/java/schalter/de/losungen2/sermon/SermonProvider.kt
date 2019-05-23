package schalter.de.losungen2.sermon

import android.content.Context
import io.reactivex.Single
import schalter.de.losungen2.dataAccess.daily.DailyVerse
import schalter.de.losungen2.dataAccess.sermon.Sermon
import java.io.File
import java.io.InputStream

// TODO move to service with notification
abstract class SermonProvider(val context: Context) {

    /**
     * Loads a sermon. That means that all data is present to download or
     * stream the sermon. Author and verse in bible is also present if possible
     * @return the download url of the sermon
     */
    abstract fun loadDownloadUrl(dailyVerse: DailyVerse): Single<String?>

    /**
     * can be called after the loadDownloadUrl method was successful
     * @return the file name
     */
    abstract fun getFileName(): String?

    /**
     * Can only be called when the sermon was loaded. Saves the sermon to
     * the file system
     * @return absolute path of the sermon on the file system
     */
    abstract fun save(): Single<String?>

    /**
     * Can be called after save was successfull
     */
    abstract fun getSermon(): Sermon?

    /**
     * Can be called after loadDownloadUrl finished.
     * @return the verse the sermon is about when known.
     */
    abstract fun getVerseInBible(): String?

    /**
     * Can be called after loadDownloadUrl finished
     * @return the author of the sermon when known
     */
    abstract fun getAuthor(): String?

    /**
     * Loads the sermon and saves the sermon to the file system
     * @return absolute path of the sermon on the file system
     */
    fun loadAndSave(dailyVerse: DailyVerse): Single<String?> {
        return loadDownloadUrl(dailyVerse).flatMap { save() }
    }

    /**
     * Call this method to save the sermon to the file system like the users
     * specified in the settings. The stream has to be closed self
     */
    protected fun saveSermon(input: InputStream): String {
        // write to private storage
        val directory = File(FileDestinations.getPrivatePath(context), SERMON_FOLDER)
        directory.mkdirs()
        val file = File(directory, getFileName())
        writeFile(file, input)

        return file.absolutePath
    }

    private fun writeFile(file: File, input: InputStream) {
        file.outputStream().use { input.copyTo(it) }
    }

    companion object {
        private const val SERMON_FOLDER = "sermons"
    }
}