package com.juniori.puzzle.ui.playvideo

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.exoplayer2.C
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
import com.juniori.puzzle.util.PuzzleDialog
import com.juniori.puzzle.util.StateManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class PlayVideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayvideoBinding
    private val viewModel: PlayVideoViewModel by viewModels()
    private var exoPlayer: ExoPlayer? = null
    private lateinit var currentVideoItem: VideoInfoEntity
    private val shareDialog: PuzzleDialog by lazy {
        PuzzleDialog(this)
            .buildAlertDialog({
                viewModel.updateVideoPrivacy(currentVideoItem)
            }, {

            }).setTitle(getString(R.string.play_share_title))
            .setMessage(getString(R.string.play_share_message))
    }
    private val hidingDialog: PuzzleDialog by lazy {
        PuzzleDialog(this)
            .buildAlertDialog({ viewModel.updateVideoPrivacy(currentVideoItem) }, {})
            .setTitle(getString(R.string.play_hiding_title))
            .setMessage(getString(R.string.play_hiding_message))
    }
    private val deleteDialog: PuzzleDialog by lazy {
        PuzzleDialog(this)
            .buildAlertDialog({
                viewModel.deleteVideo(currentVideoItem.documentId)
            },{})
            .setTitle(getString(R.string.play_delete_title))
            .setMessage(getString(R.string.play_delete_message))
    }

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

        viewModel.getPublisherInfo(currentVideoItem.ownerUid)
        viewModel.initVideoFlow(currentVideoItem)
        setItemOnClickListener()
        initCollector()

        if (savedInstanceState != null) {
            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY)
            startPosition = savedInstanceState.getLong(KEY_POSITION)
        } else {
            clearStartPosition()
        }
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT > 23) {
            initVideoPlayer(currentVideoItem.videoUrl)
            binding.playerView.onResume()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT <= 23 || exoPlayer == null) {
            initVideoPlayer(currentVideoItem.videoUrl)
            binding.playerView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT <= 23) {
            binding.playerView.onPause()
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT > 23) {
            binding.playerView.onPause()
            releasePlayer()
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        updateStartPosition()
        outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay)
        outState.putLong(KEY_POSITION, startPosition)
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
                viewModel.videoFlow.collectLatest { resource ->
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
            playWhenReady = startAutoPlay
            if (startPosition != C.TIME_UNSET) {
                seekTo(startPosition)
            }
            prepare()
            binding.playerView.player = this
        }
    }

    private fun updateStartPosition() {
        exoPlayer?.run {
            startAutoPlay = playWhenReady
            startPosition = 0L.coerceAtLeast(contentPosition)
        }
    }

    private fun clearStartPosition() {
        startAutoPlay = true
        startPosition = C.TIME_UNSET
    }

    private fun releasePlayer() {
        exoPlayer?.let { player ->
            updateStartPosition()
            player.release()
            exoPlayer = null
            binding.playerView.player = null
        }
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
                when (currentVideoItem.isPrivate) {
                    true -> {
                        shareDialog.show()
                    }
                    false -> {
                        hidingDialog.show()
                    }
                }
                true
            }
            menu.findItem(R.id.video_delete).setOnMenuItemClickListener {
                deleteDialog.show()
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
        private var startAutoPlay: Boolean = true
        private var startPosition: Long = C.TIME_UNSET
        private const val VIDEO_EXTRA_NAME = "videoInfo"
        private const val PUBLISHER_EXTRA_NAME = "publisherInfo"
        private const val KEY_POSITION = "position"
        private const val KEY_AUTO_PLAY = "auto_play"
    }
}
