package com.juniori.puzzle.ui.playvideo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.juniori.puzzle.databinding.BottomsheetPlayvideoBinding
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat

@AndroidEntryPoint
class PlayVideoBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetPlayvideoBinding? = null
    private val binding get() = _binding!!

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
        val currentUserItem = arguments?.get("publisherInfo") as UserInfoEntity
        binding.buttonOwner.text = currentUserItem.nickname
        binding.buttonDate.text =
            SimpleDateFormat("yyyy-MM-dd HH:mm").format(currentVideoItem.updateTime)
        binding.buttonLocation.text = currentVideoItem.location
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
