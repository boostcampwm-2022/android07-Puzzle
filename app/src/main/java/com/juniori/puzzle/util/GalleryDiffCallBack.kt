package com.juniori.puzzle.util

import androidx.recyclerview.widget.DiffUtil
import com.juniori.puzzle.domain.entity.VideoInfoEntity

class GalleryDiffCallBack: DiffUtil.ItemCallback<VideoInfoEntity>() {
    override fun areItemsTheSame(oldItem: VideoInfoEntity, newItem: VideoInfoEntity): Boolean {
        return oldItem.videoUrl == newItem.videoUrl
    }

    override fun areContentsTheSame(oldItem: VideoInfoEntity, newItem: VideoInfoEntity): Boolean {
        return oldItem == newItem
    }
}