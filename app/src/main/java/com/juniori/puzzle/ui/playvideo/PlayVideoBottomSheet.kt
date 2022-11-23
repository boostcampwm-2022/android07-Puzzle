package com.juniori.puzzle.ui.playvideo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.databinding.BottomsheetPlayvideoBinding
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat

@AndroidEntryPoint
class PlayVideoBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetPlayvideoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayVideoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetPlayvideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentVideoItem = arguments?.get("videoInfo") as VideoInfoEntity
        val nickname = arguments?.get("nickName") as String
        viewModel.getLoginInfoFlow.value?.let {
            if (it is Resource.Success) {
                binding.buttonOwner.text = nickname
            }
        }
        binding.buttonDate.text =
            SimpleDateFormat("yyyy-MM-dd HH:mm").format(currentVideoItem.updateTime)
        binding.buttonLocation.text = currentVideoItem.location
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
