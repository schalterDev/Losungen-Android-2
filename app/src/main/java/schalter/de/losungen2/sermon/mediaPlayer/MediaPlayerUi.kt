package schalter.de.losungen2.sermon.mediaPlayer

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import schalter.de.losungen2.R
import schalter.de.losungen2.utils.AsyncUtils

class MediaPlayerUi : FrameLayout, MediaPlayerService.StateListener, MediaPlayerService.SeekListener {

    private var title: TextView
    private var seekBar: SeekBar
    private var duration: TextView
    private var playButton: ImageView
    private var stopButton: ImageView

    private var totalTime: Int? = null

    private var serviceConnection: ServiceConnection? = null
    private var mediaPlayerService: MediaPlayerService? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initAttributes(attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle) {
        initAttributes(attributeSet)
    }

    private fun initAttributes(attributeSet: AttributeSet) {}

    init {
        val view = View.inflate(context, R.layout.view_media_player_ui, null)

        title = view.findViewById(R.id.textView_audio_title)
        seekBar = view.findViewById(R.id.audio_seekbar)
        duration = view.findViewById(R.id.textView_audio_duration)
        playButton = view.findViewById(R.id.audio_play)
        stopButton = view.findViewById(R.id.audio_stop)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) mediaPlayerService?.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        playButton.setOnClickListener {
            if (mediaPlayerService?.getState() == MediaPlayerService.State.Playing) {
                mediaPlayerService?.pauseAudio()
            } else if (mediaPlayerService?.getState() == MediaPlayerService.State.Paused) {
                mediaPlayerService?.resumeAudio()
            }
        }

        stopButton.setOnClickListener {
            mediaPlayerService?.stopAudio()
        }

        addView(view)

        this.visibility = View.GONE
    }

    /**
     * Checks if a service is running and binds to it when the id is the same.
     * When the service is already binded nothing happens
     */
    fun checkServiceIsRunningAndBind(id: String) {
        if (serviceConnection == null && // service is not bind already
                MediaPlayerService.isRunning && MediaPlayerService.actualId == id
        ) {
            bindService()
        }
    }

    /**
     * Play the audio. Creates a service and binds to it
     */
    fun playAudio(path: String, id: String) {
        val intent = Intent(context, MediaPlayerService::class.java)

        intent.action = MediaPlayerService.ACTION_START
        intent.putExtra(MediaPlayerService.EXTRA_PATH, path)
        intent.putExtra(MediaPlayerService.EXTRA_ID, id)
        context.startService(intent)

        bindService()
    }

    fun setTitle(title: String?) {
        if (title == null) {
            this.title.visibility = View.GONE
        } else {
            this.title.visibility = View.VISIBLE
            this.title.text = title
        }
    }

    private fun bindService() {
        unbindService()

        val intent = Intent(context, MediaPlayerService::class.java)

        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, serviceBinder: IBinder?) {
                val binder = serviceBinder as MediaPlayerService.MediaPlayerServiceBinder
                mediaPlayerService = binder.getService()
                mediaPlayerService?.let {
                    initService(it)
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                mediaPlayerService = null
                serviceConnection = null
                visibility = View.GONE
            }
        }.apply {
            context.bindService(intent, this, Context.BIND_AUTO_CREATE)
        }
    }

    /**
     * Unbinds the service created with playAudio. Should be called when the activity
     * or fragment is paused or destroyed.
     * When this function is called multiple times it has no effect
     */
    fun unbindService() {
        unregisterListeners()
        serviceConnection?.let { context.unbindService(it) }
        mediaPlayerService = null
        serviceConnection = null
    }

    /**
     * Unbinds and updates the ui when the service is stopped.
     * Updates the ui and registers listener otherwise
     */
    private fun initService(mediaPlayerService: MediaPlayerService) {
        if (mediaPlayerService.getState() == MediaPlayerService.State.Stopped) {
            serviceStopped()
        } else {
            visibility = View.VISIBLE

            totalTime = mediaPlayerService.getDuration()
            this.seekBar.max = totalTime!!

            registerListeners()
        }
    }

    private fun registerListeners() {
        mediaPlayerService?.addSeekListener(this)
        mediaPlayerService?.addStateListener(this)
    }

    private fun unregisterListeners() {
        mediaPlayerService?.removeSeekListener(this)
        mediaPlayerService?.removeStateListener(this)
    }

    private fun serviceStopped() {
        unbindService()
        visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    private fun updateDuration(actualTime: Int) {
        this.totalTime?.let { totalTime ->
            val actualTimeString = getTimeInMinutesAndSeconds(actualTime)
            val totalTimeString = getTimeInMinutesAndSeconds(totalTime)

            AsyncUtils.runOnMainThread {
                this.duration.text = "$actualTimeString / $totalTimeString"
                this.seekBar.progress = actualTime
            }
        }

        if (this.totalTime == null) {
            this.duration.text = ""
        }
    }

    private fun updatePlayPauseIcon(playing: Boolean) {
        AsyncUtils.runOnMainThread {
            if (playing) {
                this.playButton.setImageResource(R.drawable.ic_pause)
            } else {
                this.playButton.setImageResource(R.drawable.ic_play_arrow)
            }
        }
    }

    // ------- SERVICE LISTENERS -----------
    override fun stateChanged(oldState: MediaPlayerService.State?, newState: MediaPlayerService.State) {
        if (newState == MediaPlayerService.State.Playing) {
            updatePlayPauseIcon(true)
        } else {
            updatePlayPauseIcon(false)
        }

        if (newState == MediaPlayerService.State.Stopped) {
            serviceStopped()
        }
    }

    override fun seekChanged(newPosition: Int) {
        updateDuration(newPosition)
    }

    // --------- HELPER FUNCTIONS ------------
    private fun getTimeInMinutesAndSeconds(time: Int): String {
        val minutes = time / 1000 / 60
        val seconds = time / 1000 % 60

        var duration = "$minutes:"

        duration += if (seconds >= 10) {
            seconds.toString()
        } else {
            "0$seconds"
        }

        return duration
    }
}
