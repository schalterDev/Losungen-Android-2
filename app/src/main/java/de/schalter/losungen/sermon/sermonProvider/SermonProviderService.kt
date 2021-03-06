package de.schalter.losungen.sermon.sermonProvider

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import de.schalter.losungen.R
import de.schalter.losungen.components.exceptions.TranslatableException
import de.schalter.losungen.dataAccess.VersesDatabase
import de.schalter.losungen.dataAccess.daily.DailyVerse
import de.schalter.losungen.dataAccess.sermon.Sermon
import de.schalter.losungen.utils.AsyncUtils
import java.io.File
import java.util.*

class SermonProviderService : Service() {

    private lateinit var sermonProviderServiceNotification: SermonProviderServiceNotification

    private val myBinder = SermonProviderServiceBinder()
    private var dailyVerse: DailyVerse? = null
    private var resultListener = ArrayList<ResultListener>()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_DOWNLOAD_IF_SERMON_NOT_EXISTS ->
                checkDatabaseOrDownload(intent.extras?.getLong(EXTRA_DATE_LONG) ?: Date().time)
            ACTION_STOP_DOWNLOAD ->
                stopDownload()
        }

        return START_STICKY
    }

    @SuppressLint("CheckResult")
    fun checkDatabaseOrDownload(
            date: Long,
            sermonProvider: SermonProvider = SermonProvider.getImplementation(applicationContext)
    ) {
        initNotification()

        AsyncUtils.liveDataToSingleOptional(
                VersesDatabase.provideVerseDatabase(applicationContext).dailyVerseDao().findDailyVerseByDate(Date(date)))
                .subscribe { dailyVerse ->
                    if (dailyVerse.value != null) {
                        this.dailyVerse = dailyVerse.value
                        sermonProvider.getSermonIfExists(dailyVerse.value).subscribe { sermon ->
                            if (sermon.value != null && File(sermon.value.pathSaved).exists()) {
                                finished(sermon.value)
                            } else {
                                startDownload(sermonProvider)
                            }
                        }
                    } else {
                        error(TranslatableException(R.string.no_verses_found))
                    }
                }
    }

    @SuppressLint("CheckResult")
    private fun startDownload(sermonProvider: SermonProvider) {
        sermonProvider.loadDownloadUrl(dailyVerse!!).subscribe({ url ->
            updateNotification(SermonProviderServiceNotification.State.DownloadingSermon)
            sermonProvider.downloadAndSaveToFileAndDatabase(url).subscribe({
                finished(sermonProvider.getSermon()!!)
            }, {
                error(it)
            })
        }, {
            error(it)
        })
    }

    private fun stopDownload() {
        error(TranslatableException(R.string.aborted_by_user))
        stopService()
    }

    private fun initNotification() {
        sermonProviderServiceNotification = SermonProviderServiceNotification(applicationContext, this)
        updateNotification(SermonProviderServiceNotification.State.SearchingSermon)
    }

    private fun updateNotification(
            state: SermonProviderServiceNotification.State,
            content: String? = null) {
        startForeground(
                SermonProviderServiceNotification.NOTIFICATION_ID,
                sermonProviderServiceNotification.getNotification(state, content))
    }

    private fun error(error: Throwable) {
        if (error is TranslatableException) {
            val errorString = error.getStringForUser(applicationContext)
            updateNotification(SermonProviderServiceNotification.State.Error, errorString)
        } else {
            updateNotification(SermonProviderServiceNotification.State.Error)
        }

        stopService(false)

        resultListener.forEach { it.onError(error) }
    }

    private fun finished(sermon: Sermon) {
        resultListener.forEach { it.onResult(sermon) }
        stopService()
    }

    private fun stopService(removeNotification: Boolean = true) {
        stopForeground(removeNotification)
    }

    // ------- BINDER AND LISTENER -----------
    override fun onBind(intent: Intent?): IBinder? = myBinder

    inner class SermonProviderServiceBinder : Binder() {
        fun getService(): SermonProviderService {
            return this@SermonProviderService
        }
    }

    fun addResultListener(listener: ResultListener) {
        resultListener.add(listener)
    }

    fun removeResultListener(listener: ResultListener) {
        resultListener.remove(listener)
    }

    interface ResultListener {
        fun onResult(sermon: Sermon)
        fun onError(error: Throwable)
    }
    // ---- END BINDER AND LISTENER ----------

    companion object {
        const val ACTION_START_DOWNLOAD_IF_SERMON_NOT_EXISTS = "START_DOWNLOAD"
        const val ACTION_STOP_DOWNLOAD = "STOP_DOWNLOAD"
        const val EXTRA_DATE_LONG = "DATE_LONG"
    }
}