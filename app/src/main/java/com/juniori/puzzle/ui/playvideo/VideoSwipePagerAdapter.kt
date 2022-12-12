package com.juniori.puzzle.ui.playvideo

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.juniori.puzzle.domain.entity.VideoInfoEntity

class VideoSwipePagerAdapter(
    activity: FragmentActivity,
    private val videoFetchListener: () -> Unit
) : FragmentStateAdapter(activity) {

    private var videoInfoList: List<VideoInfoEntity> = emptyList()

    override fun getItemCount(): Int = videoInfoList.size

    override fun createFragment(position: Int): PlayVideoDetailFragment {
        return PlayVideoDetailFragment.newInstance(videoInfoList[position])
    }

    override fun onBindViewHolder(
        holder: FragmentViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (position >= videoInfoList.size - 3) {
            videoFetchListener.invoke()
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun getItemId(position: Int): Long {
        return videoInfoList[position].documentId.hashCode().toLong()
    }

    fun updateVideoInfoList(newList: List<VideoInfoEntity>) {
        val outdatedListSize = videoInfoList.size
        videoInfoList = newList
        if (outdatedListSize < newList.size) {
            notifyItemRangeInserted(outdatedListSize, newList.size - outdatedListSize)
        }
    }
}
