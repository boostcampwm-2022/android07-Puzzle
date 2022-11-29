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
import com.juniori.puzzle.util.StateManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PlayVideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayvideoBinding
    private val viewModel: PlayVideoViewModel by viewModels()
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var currentVideoItem: VideoInfoEntity

    @Inject
    lateinit var stateManager: StateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayvideoBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.vm = viewModel

        stateManager.createLoadingDialog(binding.activityPlayVideo)
        setContentView(binding.root)

        currentVideoItem = intent.extras?.get(VIDEO_EXTRA_NAME) as VideoInfoEntity
        initVideoPlayer(currentVideoItem.videoUrl)
        binding.buttonLike.text = currentVideoItem.likedCount.toString()

        viewModel.getPublisherInfo(currentVideoItem.ownerUid)
        setItemOnClickListener()
        initCollector()
    }

    private fun initCollector() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getLoginInfoFlow.collectLatest { resource ->
                    if (resource is Resource.Success) {
                        currentUserInfo = resource.result
                        viewModel.setCurrentLikeStatus(currentVideoItem, currentUserInfo.uid)
                        setMenuItems()
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.likeState.collectLatest { isLikedVideo ->
                    binding.buttonLike.setIconResource(
                        if (isLikedVideo) {
                            R.drawable.play_like_selected
                        } else {
                            R.drawable.play_like_not_selected
                        }
                    )
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getPublisherInfoFlow.collectLatest { resource ->
                    if (resource is Resource.Success) {
                        publisherUserInfo = resource.result
                        binding.materialToolbar.title = publisherUserInfo.nickname
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
                                stateManager.dismissLoadingDialog()
                                finish()
                            }
                            is Resource.Loading -> {
                                stateManager.showLoadingDialog()
                            }
                            is Resource.Failure -> {
                                stateManager.dismissLoadingDialog()
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

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updateFlow.collectLatest { resource ->
                    if (resource != null) {
                        when (resource) {
                            is Resource.Success -> {
                                stateManager.dismissLoadingDialog()
                                currentVideoItem = resource.result
                                setMenuItems()
                            }
                            is Resource.Loading -> {
                                stateManager.showLoadingDialog()
                            }
                            is Resource.Failure -> {
                                stateManager.dismissLoadingDialog()
                                resource.exception.printStackTrace()
                                Snackbar.make(
                                    binding.root,
                                    "동영상 공유상태 전환에 실패했습니다.",
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

    private fun setItemOnClickListener() {
        with(binding.materialToolbar) {
            menu.findItem(R.id.video_privacy).setOnMenuItemClickListener {
                viewModel.updateVideoPrivacy(currentVideoItem)
                true
            }
            menu.findItem(R.id.video_delete).setOnMenuItemClickListener {
                viewModel.deleteVideo(currentVideoItem.documentId)
                true
            }
            setNavigationOnClickListener {
                finish()
            }
        }
        with(binding) {
            buttonComment.setOnClickListener {
                PlayVideoBottomSheet().apply {
                    arguments = Bundle().apply {
                        putParcelable(VIDEO_EXTRA_NAME, currentVideoItem)
                        putParcelable(PUBLISHER_EXTRA_NAME, publisherUserInfo)
                    }
                }.show(supportFragmentManager, null)
            }
            buttonLike.setOnClickListener {
                viewModel.changeLikeStatus(currentVideoItem, currentUserInfo.uid)
            }
        }
    }

    companion object {
        lateinit var publisherUserInfo: UserInfoEntity
        lateinit var currentUserInfo: UserInfoEntity
        const val VIDEO_EXTRA_NAME = "videoInfo"
        const val PUBLISHER_EXTRA_NAME = "publisherInfo"
    }
}
