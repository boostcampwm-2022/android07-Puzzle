package com.juniori.puzzle.ui.playvideo

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util
import com.google.android.material.snackbar.Snackbar
import com.juniori.puzzle.R
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.databinding.ActivityPlayvideoBinding
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlayVideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayvideoBinding
    private val viewModel: PlayVideoViewModel by viewModels()
    private lateinit var exoPlayer: ExoPlayer
    private val currentVideoItem: VideoInfoEntity by lazy { intent.extras?.get("videoInfo") as VideoInfoEntity }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayvideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initVideoPlayer(currentVideoItem.videoUrl)
        setMenuItemOnClickListener()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getLoginInfoFlow.collectLatest { resource ->
                    if (resource is Resource.Success) {
                        currentUserInfo = resource.result
                        setMenuItems()
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deleteFlow.collectLatest { resource ->
                    if (resource != null) {
                        when (resource) {
                            is Resource.Success -> {
                                finish()
                            }
                            is Resource.Loading -> {
                                /** loading 화면 보여주기 */
                            }
                            is Resource.Failure -> {
                                resource.exception.printStackTrace()
                                Snackbar.make(
                                    binding.root,
                                    "동영상 삭제에 실패했습니다.",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
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

    private fun setMenuItems() {
        if (currentUserInfo.uid == currentVideoItem.ownerUid) {
            binding.materialToolbar.menu.findItem(R.id.video_privacy).title =
                if (currentVideoItem.isPrivate) {
                    getString(R.string.playvideo_to_public)
                } else {
                    getString(R.string.playvideo_to_private)
                }
        } else {
            binding.materialToolbar.menu.forEach {
                it.isVisible = false
            }
        }
    }

    private fun setMenuItemOnClickListener() {
        with(binding.materialToolbar.menu) {
            findItem(R.id.video_privacy).setOnMenuItemClickListener {
                // Update video isPrivate to Public
                true
            }
            findItem(R.id.video_delete).setOnMenuItemClickListener {
                viewModel.deleteVideo(currentVideoItem.documentId)
                true
            }
        }
    }

    companion object {
        lateinit var currentUserInfo: UserInfoEntity
    }
}