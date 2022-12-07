package com.juniori.puzzle.ui.playvideo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.juniori.puzzle.R
import com.juniori.puzzle.databinding.BottomsheetPlayvideoBinding
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat

@AndroidEntryPoint
class PlayVideoBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetPlayvideoBinding? = null
    private val binding get() = _binding!!
    private val videoInfo by lazy {
        arguments?.get("videoInfo") as VideoInfoEntity
    }
    private val publisherInfo by lazy {
        arguments?.get("publisherInfo") as UserInfoEntity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetPlayvideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setItemInformation()
    }

    @SuppressLint("SimpleDateFormat")
    private fun setItemInformation() {
        binding.itemLocation.image = R.drawable.all_location_icon.toString()
        binding.itemDate.image = R.drawable.play_calendar_icon.toString()
        binding.itemPublisher.image = publisherInfo.profileImage

        binding.itemLocation.content = videoInfo.location
        binding.itemDate.content =
            SimpleDateFormat("yyyy-MM-dd HH:mm").format(videoInfo.updateTime)
        binding.itemPublisher.content = publisherInfo.nickname

        binding.memo = videoInfo.memo
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
