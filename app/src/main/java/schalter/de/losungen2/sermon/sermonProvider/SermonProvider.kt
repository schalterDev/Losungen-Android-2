package schalter.de.losungen2.sermon.sermonProvider

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import io.reactivex.Single
import schalter.de.losungen2.dataAccess.VersesDatabase
import schalter.de.losungen2.dataAccess.daily.DailyVerse
import schalter.de.losungen2.dataAccess.sermon.Sermon
import schalter.de.losungen2.sermon.FileDestinations
import schalter.de.losungen2.sermon.erf.ErfWortZumTagSermonImplementation
import schalter.de.losungen2.utils.AsyncUtils
import java.io.File
import java.io.InputStream
import java.util.*

abstract class SermonProvider(val context: Context) {

    private var serviceConnection: ServiceConnection? = null

    /**
     * Loads a sermon. That means that after this method all data
     * is present to download the sermon.
     * Author and verse in bible is also present if possible
     * @return the download url of the sermon
     */
    abstract fun loadDownloadUrl(dailyVerse: DailyVerse): Single<String>

    /**
     * can be called after the loadDownloadUrl method was successful
     * @return the file name
     */
    abstract fun getFileName(): String?

    /**
     * Saves the sermon to the file system and the database.
     * Use the method saveSermon and saveToDatabase
     * @return absolute path of the sermon on the file system
     */
    abstract fun downloadAndSaveToFileAndDatabase(url: String): Single<String>

    /**
     * Can be called after downloadAndSaveToFileAndDatabase was successful
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
     * The name of the implementation of this provider
     * @return the name of the provider
     */
    abstract fun getProviderName(): String

    /**
     * Call this method save the sermon to the file system like the users
     * specified in the settings. The stream has to be closed self
     */
    protected fun saveSermon(input: InputStream): String {
        // write to private storage
        val directory = File(FileDestinations.getCachePath(context), SERMON_FOLDER)
        directory.mkdirs()
        val file = File(directory, getFileName())
        writeFile(file, input)

        return file.absolutePath
    }

    /**
     * The information has to be ready.
     * So the method downloadAndSaveToFileAndDatabase has to be called first
     */
    protected fun saveToDatabase() {
        getSermon()?.let { sermon ->
            val sermonDao = VersesDatabase.provideVerseDatabase(context).sermonDao()
            sermonDao.insertSermon(sermon)
        }
    }

    /**
     * Load sermon to database if it exists
     * @return null when no sermon was found or Sermon when a sermon was found in the database
     */
    internal fun getSermonIfExists(dailyVerse: DailyVerse): Single<AsyncUtils.Optional<Sermon>> {
        val sermonDao = VersesDatabase.provideVerseDatabase(context).sermonDao()
        val sermons = sermonDao.getSermonsForDailyVerseId(dailyVerse.dailyVerseId!!)

        return Single.create {
            AsyncUtils.liveDataToSingleOptional(sermons).subscribe { success ->
                if (success.value?.isNotEmpty() == true) {
                    it.onSuccess(AsyncUtils.Optional(success.value[0]))
                } else {
                    it.onSuccess(AsyncUtils.Optional(null))
                }
            }
        }
    }

    /**
     * Loads the sermon and saves the sermon to the file system and database
     * @return the sermon from database or downloaded sermon
     */
    fun getIfExistsOrLoadAndSave(date: Date): Single<Sermon> {
        return Single.create {
            val intent = Intent(context, SermonProviderService::class.java)
            context.startService(intent)

            unbindService()
            serviceConnection = object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, serviceBinder: IBinder?) {
                    val service = (serviceBinder as SermonProviderService.SermonProviderServiceBinder).getService()
                    service.addResultListener(object : SermonProviderService.ResultListener {
                        override fun onResult(sermon: Sermon) {
                            it.onSuccess(sermon)
                        }

                        override fun onError(error: Throwable) {
                            if (!it.isDisposed)
                                it.onError(error)
                        }
                    })

                    service.checkDatabaseOrDownload(date.time, this@SermonProvider)
                }

                override fun onServiceDisconnected(name: ComponentName?) {}
            }.apply {
                context.bindService(intent, this, Context.BIND_AUTO_CREATE)
            }
        }
    }

    private fun unbindService() {
        serviceConnection?.let {
            context.unbindService(it)
        }
    }

    private fun writeFile(file: File, input: InputStream) {
        file.outputStream().use { input.copyTo(it) }
    }

    companion object {
        private const val SERMON_FOLDER = "sermons"

        fun getImplementation(context: Context): SermonProvider {
            return ErfWortZumTagSermonImplementation(context)
        }
    }
}