package com.juniori.puzzle.ui.othersgallery

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.juniori.puzzle.R
import com.juniori.puzzle.databinding.FragmentOthersgalleryBinding
import com.juniori.puzzle.ui.playvideo.PlayVideoActivity
import com.juniori.puzzle.util.GalleryState
import com.juniori.puzzle.util.PlayResultConst.RESULT_DELETE
import com.juniori.puzzle.util.PlayResultConst.RESULT_TO_PRIVATE
import com.juniori.puzzle.util.PuzzleDialog
import com.juniori.puzzle.util.SortType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OthersGalleryFragment : Fragment() {

    private var _binding: FragmentOthersgalleryBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel: OthersGalleryViewModel by viewModels()
    private val activityResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_TO_PRIVATE || it.resultCode == RESULT_DELETE) {
                viewModel.getMainData()
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

        val recyclerAdapter = OtherGalleryAdapter(viewModel) {
            activityResult.launch(
                Intent(
                    requireContext(),
                    PlayVideoActivity::class.java
                ).apply {
                    this.putExtra(VIDEO_EXTRA_NAME, it)
                })
        }

        binding.recycleOtherGallery.apply {
            adapter = recyclerAdapter
            val gridLayoutManager = object : GridLayoutManager(requireContext(), 2) {
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
            viewModel.getMainData()
        }

        viewModel.list.observe(viewLifecycleOwner) { dataList ->
            binding.otherGallerySwipeRefresh.isRefreshing = false

            recyclerAdapter.submitList(dataList)

            binding.textOtherGalleryNotFound.isVisible = dataList.isEmpty()
        }

        viewModel.refresh.observe(viewLifecycleOwner) { isRefresh ->
            binding.progressOtherGallery.isVisible = isRefresh
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                GalleryState.NONE -> {
                    snackBar?.dismiss()
                }

                GalleryState.END_PAGING -> {
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

                GalleryState.NETWORK_ERROR_PAGING -> {
                    snackBar =
                        Snackbar.make(
                            view,
                            R.string.gallery_paging_error,
                            Snackbar.LENGTH_INDEFINITE
                        )
                            .setAction(R.string.gallery_retry) {
                                viewModel.getPaging()
                            }
                    snackBar?.show()
                }

                GalleryState.NETWORK_ERROR_BASE -> {
                    binding.otherGallerySwipeRefresh.isRefreshing = false
                    snackBar =
                        Snackbar.make(
                            view,
                            R.string.gallery_init_load_error,
                            Snackbar.LENGTH_INDEFINITE
                        )
                            .setAction(R.string.gallery_retry) {
                                viewModel.getMainData()
                            }
                    snackBar?.show()
                }
            }

        }

        viewModel.list.value.also { list ->
            if (list == null || list.isEmpty()) {
                viewModel.getMainData()
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
        const val ITEM_ROW_COUNT = 2
        const val RECYCLER_TOP = 0
        const val VIDEO_EXTRA_NAME = "videoInfo"

    }
}
