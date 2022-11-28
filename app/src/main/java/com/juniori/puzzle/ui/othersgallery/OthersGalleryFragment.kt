package com.juniori.puzzle.ui.othersgallery

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.juniori.puzzle.R
import com.juniori.puzzle.databinding.FragmentOthersgalleryBinding
import com.juniori.puzzle.ui.playvideo.PlayVideoActivity
import com.juniori.puzzle.util.SortType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OthersGalleryFragment : Fragment() {

    private var _binding: FragmentOthersgalleryBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel: OthersGalleryViewModel by viewModels()
    private val activityResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            viewModel.getMyData()
        }

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
            val gridLayoutManager = GridLayoutManager(requireContext(), ITEM_ROW_COUNT)
            layoutManager = gridLayoutManager
        }


        val items = resources.getStringArray(R.array.other_order_type)
        val spinnerAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)
        binding.spinnerOtherGallery.adapter = spinnerAdapter


        viewModel.list.observe(viewLifecycleOwner) { dataList ->
            recyclerAdapter.submitList(dataList)
        }

        viewModel.refresh.observe(viewLifecycleOwner) { isRefresh ->
            binding.progressOtherGallery.isVisible = isRefresh
        }

        viewModel.list.value.also { list ->
            if (list == null || list.isEmpty()) {
                viewModel.getMyData()
            }
        }

        setListener()
    }

    private fun setListener() {
        binding.spinnerOtherGallery.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
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
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}

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
