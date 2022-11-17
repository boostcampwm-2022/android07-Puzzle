package com.juniori.puzzle.ui.mygallery

import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.juniori.puzzle.databinding.FragmentMygalleryBinding


class MyGalleryFragment : Fragment() {

    private var _binding: FragmentMygalleryBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel: MyGalleryViewModel by viewModels()

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

        val recyclerAdapter = MyGalleryAdapter(viewModel)

        binding.recycleMyGallery.apply {
            adapter = recyclerAdapter
            val gridLayoutManager = GridLayoutManager(requireContext(), 2)
            layoutManager = gridLayoutManager
        }

        viewModel.list.observe(viewLifecycleOwner){ dataList ->
            recyclerAdapter.setData(dataList)
        }

        viewModel.refresh.observe(viewLifecycleOwner){ isRefresh ->
            binding.progressMyGallery.isVisible = isRefresh
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
