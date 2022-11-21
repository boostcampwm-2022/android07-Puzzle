package com.juniori.puzzle.ui.mygallery

import androidx.recyclerview.widget.DiffUtil
import com.juniori.puzzle.domain.entity.VideoInfoEntity

class GalleryDiffCallBack(private val oldItem: List<VideoInfoEntity>,
                          private val newItem: List<VideoInfoEntity>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldItem.size
    }

    override fun getNewListSize(): Int {
        return newItem.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItem[oldItemPosition].videoName == newItem[newItemPosition].videoName
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItem[oldItemPosition] == newItem[newItemPosition]
    }
}