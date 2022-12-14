package com.juniori.puzzle.ui.othersgallery

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.juniori.puzzle.R
import com.juniori.puzzle.databinding.FragmentOthersgalleryBinding
import com.juniori.puzzle.ui.playvideo.PlayVideoActivity
import com.juniori.puzzle.ui.playvideo.PlayVideoActivity.Companion.CLICKED_VIDEO_INDEX_KEY
import com.juniori.puzzle.ui.playvideo.PlayVideoActivity.Companion.GALLERY_TYPE_KEY
import com.juniori.puzzle.util.GalleryType
import com.juniori.puzzle.util.PuzzleDialog
import com.juniori.puzzle.util.SortType
import com.juniori.puzzle.util.VideoFetchingState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OthersGalleryFragment : Fragment() {

    private var _binding: FragmentOthersgalleryBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel: OthersGalleryViewModel by viewModels()
    private val recyclerAdapter: OthersGalleryAdapter by lazy {
        OthersGalleryAdapter(viewModel) { clickedIndex ->
            playVideoActivityLauncher.launch(
                Intent(requireContext(), PlayVideoActivity::class.java).apply {
                    putExtra(CLICKED_VIDEO_INDEX_KEY, clickedIndex)
                    putExtra(GALLERY_TYPE_KEY, GalleryType.OTHERS)
                }
            )
        }
    }
    private val playVideoActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val lastViewedPosition = result.data?.extras?.getInt(
                    PlayVideoActivity.LAST_VIEWED_VIDEO_INDEX_KEY,
                    0
                )
                    ?: return@registerForActivityResult
                binding.recycleOtherGallery.scrollToPosition(lastViewedPosition)
            }
        }

    private var snackBar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOthersgalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recycleOtherGallery.apply {
            adapter = recyclerAdapter
            val gridLayoutManager = object : GridLayoutManager(requireContext(), resources.getInteger(R.integer.grid_column)) {
                override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                    if (lp != null) {
                        if (lp.height < height / 3) {
                            lp.height = height / 3
                        }
                    }
                    return super.checkLayoutParams(lp)
                }
            }
            layoutManager = gridLayoutManager
        }

        binding.otherGallerySwipeRefresh.setOnRefreshListener {
            viewModel.fetchFirstVideoPage()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.videoList.collectLatest { videoList ->
                    binding.otherGallerySwipeRefresh.isRefreshing = false

                    recyclerAdapter.submitList(videoList)

                    binding.textOtherGalleryNotFound.isVisible = videoList.isEmpty()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.videoFetchingState.collectLatest { state ->
                    binding.progressOtherGallery.isVisible = false

                    when (state) {
                        VideoFetchingState.NONE -> {
                            snackBar?.dismiss()
                        }

                        VideoFetchingState.LOADING -> {
                            binding.progressOtherGallery.isVisible = true
                        }

                        VideoFetchingState.NO_MORE_VIDEO -> {
                            snackBar = Snackbar.make(
                                view,
                                R.string.gallery_end_paging,
                                Snackbar.LENGTH_SHORT
                            ).apply {
                                setAction(R.string.gallery_check) {
                                    dismiss()
                                }
                            }
                            snackBar?.show()
                        }

                        VideoFetchingState.NETWORK_ERROR_PAGING -> {
                            snackBar = Snackbar.make(
                                view,
                                R.string.gallery_paging_error,
                                Snackbar.LENGTH_INDEFINITE
                            ).setAction(R.string.gallery_retry) {
                                viewModel.fetchNextVideoPage()
                            }
                            snackBar?.show()
                        }

                        VideoFetchingState.NETWORK_ERROR_BASE -> {
                            binding.otherGallerySwipeRefresh.isRefreshing = false
                            snackBar = Snackbar.make(
                                view,
                                R.string.gallery_init_load_error,
                                Snackbar.LENGTH_INDEFINITE
                            ).setAction(R.string.gallery_retry) {
                                viewModel.fetchFirstVideoPage()
                            }
                            snackBar?.show()
                        }
                    }
                }
            }
        }

        setListener()
    }

    private fun setListener() {
        val items = resources.getStringArray(R.array.other_order_type)
        val popup =
            PuzzleDialog(requireContext()).buildListPopup(binding.spinnerOtherGallery, items)

        popup.setListPopupItemListener { parent, view, position, id ->
            binding.spinnerOtherGallery.text = items[position]
            when (position) {
                0 -> {
                    if (viewModel.setOrderType(SortType.NEW)) {
                        binding.recycleOtherGallery.scrollToPosition(RECYCLER_TOP)
                    }
                }

                1 -> {
                    if (viewModel.setOrderType(SortType.LIKE)) {
                        binding.recycleOtherGallery.scrollToPosition(RECYCLER_TOP)
                    }
                }
            }

            popup.dismissPopupList()
        }

        binding.spinnerOtherGallery.text = items[0]
        binding.spinnerOtherGallery.setOnClickListener {
            popup.showPopupList()
        }

        binding.searchOtherGallery.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.setQueryText(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    viewModel.setQueryText(newText)
                }
                return false
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val RECYCLER_TOP = 0
    }
}
