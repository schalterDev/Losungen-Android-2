package schalter.de.losungen2.sermon.mediaPlayer

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class MediaPlayerService : Service() {

    private var mediaPlayerServiceNotification: MediaPlayerServiceNotification? = null

    private var mediaPlayer: MediaPlayer? = null
    private val myBinder = MediaPlayerServiceBinder()
    private var state = State.Initial

    private var timerTask: TimerTask? = null

    private val stateListener = ArrayList<StateListener>()
    private val seekListener = ArrayList<SeekListener>()

    override fun onBind(intent: Intent?): IBinder? {
        return myBinder
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
    }

    override fun onCreate() {
        super.onCreate()
        isRunning = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                if (isRunning) {
                    stopAudio(false)
                }

                if (intent.extras?.getBoolean(EXTRA_SHOW_NOTIFICATION, true) == true) {
                    initNotification()
                }

                startAudio(intent.extras?.getString(EXTRA_PATH), intent.extras?.getString(EXTRA_ID))
            }
            ACTION_PAUSE -> {
                pauseAudio()
            }
            ACTION_RESUME -> {
                resumeAudio()
            }
            ACTION_STOP -> {
                stopAudio()
            }
        }

        return START_STICKY
    }

    private fun startAudio(path: String?, id: String?) {
        if (path != null && id != null) {
            actualId = id

            mediaPlayer = MediaPlayer().apply {
                this.setOnCompletionListener { stopAudio() }

                this.setDataSource(path)
                setState(State.Preparing)
                this.prepare()
            }

            resumeAudio()
        } else {
            onError()
        }
    }

    private fun initNotification() {
        mediaPlayerServiceNotification = MediaPlayerServiceNotification(
                applicationContext,
                this,
                "Test") // TODO add title

        updateNotification()
    }

    private fun updateNotification() {
        mediaPlayerServiceNotification?.let {
            startForeground(
                    MediaPlayerServiceNotification.NOTIFICATION_ID,
                    it.getNotification())
        }
    }

    private fun setState(newState: State) {
        this.stateListener.forEach {
            it.stateChanged(this.state, newState)
        }
        this.state = newState
        updateNotification()
    }

    private fun onError() {
        setState(State.Error)
        stopAudio()
    }

    fun getState(): State = this.state

    fun pauseAudio() {
        setState(State.Paused)
        mediaPlayer?.pause()
        timerTask?.cancel()
    }

    fun resumeAudio() {
        setState(State.Playing)
        mediaPlayer?.start()
        timerTask = Timer().schedule(0L, refreshRateMillis) {
            if (state != State.Stopped) {
                mediaPlayer?.let { mediaPlayer ->
                    seekListener.forEach {
                        it.seekChanged(mediaPlayer.currentPosition)
                    }
                }
            }
        }
    }

    fun stopAudio(stopService: Boolean = true) {
        timerTask?.cancel()

        if (state != State.Initial && state != State.Preparing && state != State.Stopped) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }

        setState(State.Stopped)

        if (stopService) {
            if (mediaPlayerServiceNotification == null)
                stopSelf()
            else
                stopForeground(true)
        }
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
        seekListener.forEach { it.seekChanged(position) }
    }

    fun getDuration() = mediaPlayer?.duration

    // -------- LISTENER ---------
    fun addStateListener(listener: StateListener) {
        listener.stateChanged(null, this.state)
        this.stateListener.add(listener)
    }

    fun removeStateListener(listener: StateListener) = this.stateListener.remove(listener)

    fun addSeekListener(listener: SeekListener) {
        mediaPlayer?.let {
            listener.seekChanged(it.currentPosition)
        }
        this.seekListener.add(listener)
    }

    fun removeSeekListener(listener: SeekListener) = this.seekListener.remove(listener)

    interface StateListener {
        fun stateChanged(oldState: State?, newState: State)
    }

    interface SeekListener {
        fun seekChanged(newPosition: Int)
    }
    // -------- END LISTENER ---------

    inner class MediaPlayerServiceBinder : Binder() {
        fun getService(): MediaPlayerService {
            return this@MediaPlayerService
        }
    }

    enum class State {
        Initial,
        Preparing,
        Playing,
        Paused,
        Stopped,
        Error
    }

    companion object {
        const val ACTION_START = "START"
        const val ACTION_PAUSE = "PAUSE"
        const val ACTION_RESUME = "RESUME"
        const val ACTION_STOP = "STOP"
        const val EXTRA_PATH = "PATH"
        const val EXTRA_ID = "ID"
        const val EXTRA_SHOW_NOTIFICATION = "SHOW_NOTIFICATION" // default value is true

        const val refreshRateMillis = 500L

        var isRunning = false
        var actualId: String? = null
    }
}