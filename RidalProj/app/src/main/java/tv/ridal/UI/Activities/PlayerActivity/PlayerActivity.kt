package tv.ridal.UI.Activities.PlayerActivity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import tv.ridal.UI.Activities.BaseActivity
import tv.ridal.HDRezka.Streams.Stream
import tv.ridal.UI.Layout.LayoutHelper

class PlayerActivity : BaseActivity()
{
    private lateinit var streams: ArrayList<Stream>

    companion object
    {

    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        streams = intent.extras!!.getParcelableArrayList("key")!!

        createUI()
    }

    override fun onStart()
    {
        super.onStart()

        if (Build.VERSION.SDK_INT >= 24) {
            createPlayer()
        }
    }

    override fun onResume()
    {
        super.onResume()

        hideSystemUi()
        if (Build.VERSION.SDK_INT < 24 || player == null) {
            createPlayer()
        }
    }

    override fun onPause()
    {
        super.onPause()

        if (Build.VERSION.SDK_INT < 24) {
            destroyPlayer()
        }
    }

    override fun onStop()
    {
        super.onStop()

        if (Build.VERSION.SDK_INT >= 24) {
            destroyPlayer()
        }
    }

    private fun hideSystemUi()
    {
        playerView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    private lateinit var rootFrame: FrameLayout
    private lateinit var playerView: PlayerView

    private  var player: SimpleExoPlayer? = null

    private fun createUI()
    {
        rootFrame = FrameLayout(this)
        setContentView(rootFrame)

        playerView = PlayerView(this)
        rootFrame.addView(playerView, LayoutHelper.createFrame(
            LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT
        ))
    }

    private fun createPlayer()
    {
        player = SimpleExoPlayer.Builder(this)
            .build()
            .also {
                playerView.player = it

                val mediaItem = MediaItem.fromUri(streams.last().url)
                it.setMediaItem(mediaItem)
            }

        player!!.apply {
            playWhenReady = this@PlayerActivity.playWhenReady
            seekTo(currentWindow, playbackPosition)
            prepare()
        }
    }

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    private fun destroyPlayer()
    {
        player?.run {
            playbackPosition = this.currentPosition
            currentWindow = this.currentWindowIndex
            playWhenReady = this.playWhenReady
            release()
        }
        player = null
    }
}





































//