package com.brend.serialsnow.views

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.NavUtils
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.MediaRouteButton
import android.support.v7.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import com.brend.serialsnow.R
import com.brend.serialsnow.databinding.ActivityPlayerBinding
import com.brend.serialsnow.viewmodels.PlayerViewModel
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsManifest
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_player.*
import pl.droidsonroids.casty.Casty
import pl.droidsonroids.casty.MediaData

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class PlayerActivity : AppCompatActivity() {
    private val mHideHandler = Handler()
    private val mHidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        simple_player.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    private val mShowPart2Runnable = Runnable {
        supportActionBar?.show()
    }
    private var mVisible: Boolean = false
    private val mHideRunnable = Runnable { hide() }


    lateinit var binding: ActivityPlayerBinding

    private var playWhenReady: Boolean = true

    private var currentWindow: Int = 0

    private var playbackPosition: Long = 0

    lateinit private var videoTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_player)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mVisible = true

        // Set up the user interaction to manually show or hide the system UI.
        simple_player.setOnClickListener { toggle() }

        val viewModel = ViewModelProviders.of(this).get(PlayerViewModel::class.java)

        viewModel.currentStream = intent.extras.getString("streamUri")
        videoTitle = intent.extras.getString("title")

        binding.viewModel = viewModel

        (findViewById<ImageButton>(R.id.quality_btn)).setOnClickListener {
            (findViewById<Spinner>(R.id.format_spinner)).performClick()
            hide()
        }

        (findViewById<Spinner>(R.id.format_spinner)).onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                hide()
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val selectedValue = parent?.getItemAtPosition(pos)
                val uri = viewModel.availableFormats[selectedValue]
                if (uri != viewModel.currentStream) {
                    viewModel.currentStream = uri
                    releasePlayer()
                    initializePlayer()
                }
                hide()
            }
        }

        val castContext = ContextThemeWrapper(applicationContext, android.support.v7.mediarouter.R.style.Theme_MediaRouter)
        val a = castContext.obtainStyledAttributes(null,
                android.support.v7.mediarouter.R.styleable.MediaRouteButton,
                android.support.v7.mediarouter.R.attr.mediaRouteButtonStyle, 0)

        val drawable = a.getDrawable(android.support.v7.mediarouter.R.styleable.MediaRouteButton_externalRouteEnabledDrawable)
        a.recycle()

        DrawableCompat.setTint(drawable, Color.WHITE)

        val mediaRouteBtn = findViewById<MediaRouteButton>(R.id.media_route_button)
        mediaRouteBtn.setRemoteIndicatorDrawable(drawable)

        val casty = Casty.create(this)
        casty.setUpMediaRouteButton(mediaRouteBtn)
        casty.setOnConnectChangeListener(object : Casty.OnConnectChangeListener {
            override fun onConnected() {
                val mediaData = MediaData.Builder(binding.viewModel?.currentStream)
                        .setStreamType(MediaData.STREAM_TYPE_BUFFERED)
                        .setContentType("videos/mp4")
                        .setMediaType(MediaData.MEDIA_TYPE_TV_SHOW)
                        .setTitle(videoTitle)
                        .build()

                casty.player.loadMediaAndPlay(mediaData)
            }

            override fun onDisconnected() {}

        })

        binding.executePendingBindings()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        mVisible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
        simple_player.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        mVisible = true

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun initializePlayer() {
        val bandwidthMeter = DefaultBandwidthMeter()
        val adaptiveTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(adaptiveTrackSelectionFactory)

        binding.simplePlayer.player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
        binding.simplePlayer.player.playWhenReady = playWhenReady
        binding.simplePlayer.player.seekTo(currentWindow, playbackPosition)

        binding.viewModel?.currentStream?.let {
            val defaultBandwidthMeter = DefaultBandwidthMeter()
            val userAgent = Util.getUserAgent(this, "Serial-now")
            val dataSourceFactory = DefaultDataSourceFactory(this, userAgent, defaultBandwidthMeter)

            binding.simplePlayer.player.prepare(HlsMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(it)))
            binding.simplePlayer.player.addListener(object : Player.EventListener {
                override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
                }

                override fun onSeekProcessed() {

                }

                override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
                }

                override fun onPlayerError(error: ExoPlaybackException?) {

                }

                override fun onLoadingChanged(isLoading: Boolean) {
                }

                override fun onPositionDiscontinuity(reason: Int) {

                }

                override fun onRepeatModeChanged(repeatMode: Int) {

                }

                override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

                }

                override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {
                    val variants = (manifest as HlsManifest).masterPlaylist.variants

                    binding.viewModel?.currentStream = manifest.mediaPlaylist.baseUri

                    if (binding.viewModel?.availableFormats?.isEmpty() == true) {
                        val formats = ArrayList<String>()
                        for (item in variants) {
                            formats.add("${item.format.width}x${item.format.height}")
                            binding.viewModel?.availableFormats?.put("${item.format.width}x${item.format.height}", item.url)
                        }

                        (findViewById<Spinner>(R.id.format_spinner)).adapter = ArrayAdapter<String>(binding.root.context, R.layout.support_simple_spinner_dropdown_item, formats)
                    }
                }

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

                }

            })
        }
    }

    private fun releasePlayer() {
        if (binding.simplePlayer.player != null) {
            playbackPosition = binding.simplePlayer.player.currentPosition
            currentWindow = binding.simplePlayer.player.currentWindowIndex
            playWhenReady = binding.simplePlayer.player.playWhenReady
            binding.simplePlayer.player.release()
            binding.simplePlayer.player = null
        }
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300
    }
}
