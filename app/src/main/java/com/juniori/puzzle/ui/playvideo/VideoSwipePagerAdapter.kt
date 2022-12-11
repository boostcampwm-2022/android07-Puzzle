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
    private val videoInfoIdSet = HashSet<Long>()

    override fun getItemCount(): Int = videoInfoList.size

    override fun createFragment(position: Int): PlayVideoDetailFragment {
        return PlayVideoDetailFragment.newInstance(videoInfoList[position])
    }

    fun updateVideoInfoList(newList: List<VideoInfoEntity>) {
        val outdatedListSize = videoInfoList.size
        videoInfoList = newList
        if (outdatedListSize < newList.size) {
            addNewItemsId(outdatedListSize, newList.size - outdatedListSize)
            notifyItemRangeInserted(outdatedListSize, newList.size - outdatedListSize)
        }
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

    override fun containsItem(itemId: Long): Boolean {
        return videoInfoIdSet.contains(itemId)
    }

    private fun addNewItemsId(start: Int, size: Int) {
        for (position in start until start + size) {
            videoInfoIdSet.add(videoInfoList[position].documentId.hashCode().toLong())
        }
    }

    fun notifyVideoRemoved(removedDocumentId: String, removedPosition: Int) {
        videoInfoIdSet.remove(removedDocumentId.hashCode().toLong())
        notifyItemRemoved(removedPosition)
    }
}
