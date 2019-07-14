package schalter.de.losungen2.sermon.mediaPlayer

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import schalter.de.losungen2.R

class MediaPlayerUi : FrameLayout {

    private var title: TextView
    private var seekBar: SeekBar
    private var duration: TextView
    private var playButton: ImageView
    private var stopButton: ImageView

    private var totalTime: Int? = null
    private var actualTime: Int? = null

    private var serviceConnection: ServiceConnection? = null
    private var mediaPlayerService: MediaPlayerService? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initAttributes(attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle) {
        initAttributes(attributeSet)
    }

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
            } else {
                mediaPlayerService?.resumeAudio()
            }
        }

        stopButton.setOnClickListener {
            mediaPlayerService?.stopAudio()
        }

        addView(view)

        this.visibility = View.GONE
    }

    private fun initAttributes(attributeSet: AttributeSet) {}

    fun checkServiceIsRunningAndBind(id: String) {
        if (serviceConnection == null && // service is not bind already
                MediaPlayerService.isRunning && MediaPlayerService.actualId == id
        ) {
            bindService()
        }
    }

    fun playAudio(path: String, id: String) {
        val intent = Intent(context, MediaPlayerService::class.java)

        intent.action = MediaPlayerService.ACTION_START
        intent.putExtra(MediaPlayerService.EXTRA_PATH, path)
        intent.putExtra(MediaPlayerService.EXTRA_ID, id)
        context.startService(intent)

        bindService()
    }

    private fun bindService() {
        val intent = Intent(context, MediaPlayerService::class.java)

        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as MediaPlayerService.MediaPlayerServiceBinder
                this@MediaPlayerUi.mediaPlayerService = binder.getService()
                this@MediaPlayerUi.visibility = View.VISIBLE
                this@MediaPlayerUi.initService()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                this@MediaPlayerUi.mediaPlayerService = null
                this@MediaPlayerUi.visibility = View.GONE
            }
        }

        context.bindService(intent, serviceConnection!!, Context.BIND_AUTO_CREATE)
    }

    fun unbindService() {
        serviceConnection?.let { context.unbindService(it) }
        mediaPlayerService = null
        serviceConnection = null
    }

    private fun initService() {
        if (mediaPlayerService != null) {
            totalTime = mediaPlayerService!!.getDuration()
            this.seekBar.max = totalTime!!

            mediaPlayerService!!.addSeekListener(object : MediaPlayerService.SeekListener {
                override fun seekChanged(newPosition: Int) {
                    this@MediaPlayerUi.actualTime = newPosition
                    this@MediaPlayerUi.updateDuration()
                }
            })

            mediaPlayerService!!.addStateListener(object : MediaPlayerService.StateListener {
                override fun stateChanged(oldState: MediaPlayerService.State?, newState: MediaPlayerService.State) {
                    if (newState == MediaPlayerService.State.Playing) {
                        updateIcon(playing = true)
                    } else {
                        updateIcon(playing = false)
                    }

                    if (newState == MediaPlayerService.State.Stopped) {
                        unbindService()
                        visibility = View.GONE
                    }
                }
            })
        }
    }

    fun setTitle(title: String?) {
        if (title == null) {
            this.title.visibility = View.GONE
        } else {
            this.title.visibility = View.VISIBLE
            this.title.text = title
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateDuration() {
        if (this.actualTime != null && this.totalTime != null) {
            val actualTime = getTimeInMinutesAndSeconds(this.actualTime!!)
            val totalTime = getTimeInMinutesAndSeconds(this.totalTime!!)

            Handler(Looper.getMainLooper()).post {
                this.duration.text = "$actualTime / $totalTime"
                this.seekBar.progress = this.actualTime!!
            }
        } else {
            this.duration.text = ""
        }
    }

    private fun updateIcon(playing: Boolean) {
        Handler(Looper.getMainLooper()).post {
            if (playing) {
                this.playButton.setImageResource(R.drawable.ic_pause)
            } else {
                this.playButton.setImageResource(R.drawable.ic_play_arrow)
            }
        }
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
