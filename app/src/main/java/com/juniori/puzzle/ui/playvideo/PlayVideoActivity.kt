package com.juniori.puzzle.ui.playvideo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util
import com.juniori.puzzle.R
import com.juniori.puzzle.databinding.ActivityPlayvideoBinding
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayVideoActivity : AppCompatActivity() {
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var binding: ActivityPlayvideoBinding
    private val currentVideoItem: VideoInfoEntity by lazy { intent.extras?.get("videoDetail") as VideoInfoEntity }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayvideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val uid = "xWXP9ui0RwhhS18MngcVGvz9mBf1"
        setMenuItems(uid)
        initVideoPlayer(currentVideoItem.videoUrl)
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

    private fun setMenuItems(uid: String) {
        if (uid == currentVideoItem.ownerUid) {
            if (currentVideoItem.isPrivate) {
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

    private fun setMenuItemOnClickListener() {
        with(binding.materialToolbar.menu) {
            findItem(R.id.video_public).setOnMenuItemClickListener {
                // Update video isPrivate to Public
                true
            }
            findItem(R.id.video_private).setOnMenuItemClickListener {
                // Update video isPrivate to Private
                true
            }
            findItem(R.id.video_delete).setOnMenuItemClickListener {
                // Delete video
                true
            }
        }
    }
}