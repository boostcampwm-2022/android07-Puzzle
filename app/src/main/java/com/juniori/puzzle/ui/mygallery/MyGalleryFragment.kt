package com.juniori.puzzle.ui.mygallery

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.juniori.puzzle.R
import com.juniori.puzzle.databinding.FragmentMygalleryBinding
import com.juniori.puzzle.ui.othersgallery.VideoFetchingState
import com.juniori.puzzle.ui.playvideo.PlayVideoActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyGalleryFragment : Fragment() {

    private var _binding: FragmentMygalleryBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel: MyGalleryViewModel by viewModels()
    private val activityResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val lastViewedPosition = result.data?.extras?.getInt(
                    PlayVideoActivity.LAST_VIEWED_VIDEO_INDEX_KEY,
                    0
                ) ?: return@registerForActivityResult
                binding.recycleMyGallery.scrollToPosition(lastViewedPosition)
            }
        }

    private var snackBar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMygalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getMyData()

        val recyclerAdapter = MyGalleryAdapter(viewModel) { clickedIndex ->
            activityResult.launch(
                Intent(requireContext(), PlayVideoActivity::class.java).apply {
                    putExtra(PlayVideoActivity.CLICKED_VIDEO_INDEX_KEY, clickedIndex)
                }
            )
        }

        binding.recycleMyGallery.apply {
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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.videoList.collectLatest { dataList ->
                    binding.mygallerySwipeRefresh.isRefreshing = false

                    recyclerAdapter.submitList(dataList)

                    binding.mygalleryAddVideoBtn.isVisible = dataList.isEmpty()
                    binding.mygalleryAddVideoText.isVisible = dataList.isEmpty()
                }
            }
        }

        binding.mygalleryAddVideoBtn.setOnClickListener {
            view.findNavController().navigate(R.id.bottomsheet_main_addvideo)
        }

        binding.mygallerySwipeRefresh.setOnRefreshListener {
            viewModel.getMyData()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.videoFetchingState.collectLatest { state ->
                    binding.progressMyGallery.isVisible = false

                    when (state) {
                        VideoFetchingState.NONE -> {
                            snackBar?.dismiss()
                        }

                        VideoFetchingState.Loading -> {
                            binding.progressMyGallery.isVisible = true
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
                            snackBar =
                                Snackbar.make(
                                    view,
                                    R.string.gallery_paging_error,
                                    Snackbar.LENGTH_INDEFINITE
                                )
                                    .setAction(R.string.gallery_retry) {
                                        viewModel.getPaging(recyclerAdapter.itemCount)
                                    }
                            snackBar?.show()
                        }

                        VideoFetchingState.NETWORK_ERROR_BASE -> {
                            binding.mygallerySwipeRefresh.isRefreshing = false
                            snackBar =
                                Snackbar.make(
                                    view,
                                    R.string.gallery_init_load_error,
                                    Snackbar.LENGTH_INDEFINITE
                                )
                                    .setAction(R.string.gallery_retry) {
                                        viewModel.getMyData()
                                    }
                            snackBar?.show()
                        }
                    }
                }
            }
        }

        binding.searchMyGallery.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
}
