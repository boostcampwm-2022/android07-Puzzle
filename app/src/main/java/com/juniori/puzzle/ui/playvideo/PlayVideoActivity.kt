package com.juniori.puzzle.ui.playvideo

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.juniori.puzzle.R
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.databinding.ActivityPlayvideoBinding
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.util.SortType
import com.juniori.puzzle.util.StateManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PlayVideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayvideoBinding
    private val viewModel: PlayVideoViewModel by viewModels()
    private lateinit var swipePagerAdapter: VideoSwipePagerAdapter
    private val currentVideoItem: VideoInfoEntity?
        get() = viewModel.currentVideoFlow.value

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
            }, {})
            .setTitle(getString(R.string.play_delete_title))
            .setMessage(getString(R.string.play_delete_message))
    }

    @Inject
    lateinit var stateManager: StateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayvideoBinding.inflate(layoutInflater)

        stateManager.createLoadingDialog(binding.activityPlayVideo)
        setContentView(binding.root)

        sendInitialDataToViewModel()
        initializeSwipeViewPager()
        setItemOnClickListener()
        initCollector()
    }

    private fun sendInitialDataToViewModel() {
        with(intent) {
            viewModel.setData(
                getStringExtra(QUERY_KEY) ?: "",
                getSerializableExtra(SORT_TYPE_KEY) as? SortType ?: SortType.NEW,
                getIntExtra(CLICKED_VIDEO_INDEX_KEY, 0)
            )
        }
    }

    private fun initializeSwipeViewPager() {
        swipePagerAdapter = VideoSwipePagerAdapter(this) {
            viewModel.fetchMoreVideos()
        }
        binding.swipedVideoPlayerPager.apply {
            adapter = swipePagerAdapter

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewModel.setCurrentVideoIndex(position)
                }
            })
        }
    }

    private fun setMenuItems(currentUserId: String, videoOwnerId: String) {
        if (currentUserId == videoOwnerId) {
            binding.materialToolbar.menu.findItem(R.id.video_privacy).title =
                if (currentVideoItem?.isPrivate ?: return) {
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
                val currentVideo = viewModel.currentVideoFlow.value
                    ?: return@setOnMenuItemClickListener true
                when (currentVideo.isPrivate) {
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
    }

    private fun initCollector() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentVideoFlow.collectLatest { videoInfo ->
                    setMenuItems(
                        currentUserId = viewModel.currentUserInfo?.uid ?: return@collectLatest,
                        videoOwnerId = videoInfo?.ownerUid ?: return@collectLatest
                    )
                    viewModel.getPublisherInfo(videoInfo.ownerUid)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.videoListFlow.collectLatest { videoList ->
                    swipePagerAdapter.updateVideoInfoList(videoList)

                    // 처음에 클릭된 영상의 index로 ViewPager의 position를 이동시키기 위함
                    if (binding.swipedVideoPlayerPager.currentItem != viewModel.currentVideoIndex) {
                        binding.swipedVideoPlayerPager.setCurrentItem(
                            viewModel.currentVideoIndex,
                            false
                        )
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getPublisherInfoFlow.collectLatest { resource ->
                    if (resource is Resource.Success) {
                        val publisherUserInfo = resource.result
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
                viewModel.privacyFlow.collectLatest { resource ->
                    if (resource != null) {
                        when (resource) {
                            is Resource.Success -> {
                                stateManager.dismissLoadingDialog()
                                setMenuItems(
                                    currentUserId = viewModel.currentUserInfo?.uid ?: return@collectLatest,
                                    videoOwnerId = resource.result.ownerUid
                                )
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

    override fun onDestroy() {
        setResult(
            RESULT_OK,
            Intent().apply {
                this.putExtra(LAST_VIEWED_VIDEO_INDEX_KEY, viewModel.currentVideoIndex)
            }
        )
        super.onDestroy()
    }

    companion object {
        const val LAST_VIEWED_VIDEO_INDEX_KEY = "LAST_VIEWED_VIDEO_INDEX"
        const val CLICKED_VIDEO_INDEX_KEY = "CLICKED_VIDEO_INDEX"
        const val QUERY_KEY = "QUERY"
        const val SORT_TYPE_KEY = "SORT_TYPE"
    }
}
