package com.juniori.puzzle.ui.playvideo

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util
import com.juniori.puzzle.R
import com.juniori.puzzle.data.firebase.dto.VideoDetail
import com.juniori.puzzle.databinding.ActivityPlayvideoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayVideoActivity : AppCompatActivity() {
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var binding: ActivityPlayvideoBinding
    private val currentVideoItem: VideoDetail by lazy { intent.extras?.get("videoDetail") as VideoDetail }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayvideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val uid = "xWXP9ui0RwhhS18MngcVGvz9mBf1"
        setMenu("")
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

    private fun setMenu(uid: String) {
        if (uid == currentVideoItem.ownerUid.stringValue) {
            if (currentVideoItem.isPrivate.booleanValue) {
                binding.materialToolbar.menu.findItem(R.id.video_private).isVisible = false
            } else {
                binding.materialToolbar.menu.findItem(R.id.video_public).isVisible = false
            }
        } else {
            binding.materialToolbar.menu.forEach {
                it.isVisible = false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (currentVideoItem.isPrivate.booleanValue) {
            val inflater: MenuInflater = menuInflater
            inflater.inflate(R.menu.playvideo_menu, menu)
        }
        return true
    }
}