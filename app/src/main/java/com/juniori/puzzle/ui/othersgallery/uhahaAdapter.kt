package com.juniori.puzzle.ui.othersgallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.juniori.puzzle.databinding.ItemGalleryRecyclerBinding
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.util.GalleryDiffCallBack

class OtherGalleryAdapterk(
    val viewModel: OthersGalleryViewModelk,
    private val onClick: (position: Int) -> Unit
) : ListAdapter<VideoInfoEntity, OtherGalleryAdapterk.ViewHolder>(
    GalleryDiffCallBack()
) {

    class ViewHolder(
        val binding: ItemGalleryRecyclerBinding,
        val onClick: (position: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onClick(layoutPosition)
            }
        }

        fun bind(item: VideoInfoEntity) {
            binding.data = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemGalleryRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
        if (position == itemCount - LOADING_FLAG_NUM) {
            viewModel.getPaging()
        }
    }

    companion object {
        const val VISIBLE_ITEM_COUNT = 3
        const val LOADING_FLAG_NUM = 1
    }
}
