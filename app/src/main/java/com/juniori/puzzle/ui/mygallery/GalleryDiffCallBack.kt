package com.juniori.puzzle.ui.mygallery

import androidx.recyclerview.widget.DiffUtil
import com.juniori.puzzle.domain.entity.VideoInfoEntity

class GalleryDiffCallBack: DiffUtil.ItemCallback<VideoInfoEntity>() {
    override fun areItemsTheSame(oldItem: VideoInfoEntity, newItem: VideoInfoEntity): Boolean {
        return oldItem.videoName == newItem.videoName
    }

    override fun areContentsTheSame(oldItem: VideoInfoEntity, newItem: VideoInfoEntity): Boolean {
        return oldItem == newItem
    }
}