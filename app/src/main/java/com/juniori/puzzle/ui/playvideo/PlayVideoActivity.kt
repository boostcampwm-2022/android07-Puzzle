package com.juniori.puzzle.ui.playvideo

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util
import com.juniori.puzzle.R
import com.juniori.puzzle.data.firebase.dto.VideoDetail
import com.juniori.puzzle.databinding.ActivityPlayVideoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayVideoActivity : AppCompatActivity() {
    private val currentVideoItem: VideoDetail by lazy {
        intent.extras?.get("VideoDetail") as VideoDetail
    }
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var binding: ActivityPlayVideoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVideoPlayer(currentVideoItem.videoUrl.stringValue)
    }

    private fun initVideoPlayer(uri: String) {
        val factory = DefaultHttpDataSource.Factory().apply {
            setUserAgent(Util.getUserAgent(this@PlayVideoActivity, applicationInfo.name))
        }
        exoPlayer = ExoPlayer.Builder(this).build().apply {
            setMediaSource(
                ProgressiveMediaSource.Factory(factory).createMediaSource(MediaItem.fromUri(uri))
            )
            prepare()
        }
        binding.playerView.player = exoPlayer
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.play_video_menu, menu)
        return true
    }
}