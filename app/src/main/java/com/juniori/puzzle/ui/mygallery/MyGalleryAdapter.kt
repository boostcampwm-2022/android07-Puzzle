package com.juniori.puzzle.ui.mygallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.juniori.puzzle.databinding.ItemGalleryRecyclerBinding
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.util.GalleryDiffCallBack


class MyGalleryAdapter(val viewModel: MyGalleryViewModel) : ListAdapter<VideoInfoEntity,MyGalleryAdapter.ViewHolder>(
    GalleryDiffCallBack()
) {
    private var lastPaging = 0
    class ViewHolder(val binding: ItemGalleryRecyclerBinding, val height: Int) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VideoInfoEntity) {
            binding.root.layoutParams.height = height/ VISIBLE_ITEM_COUNT
            binding.data = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemGalleryRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding, parent.height)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
        if(itemCount!= lastPaging && position == itemCount - LOADING_FLAG_NUM){
            lastPaging = itemCount
            viewModel.getPaging(itemCount)
        }
    }


    companion object{
        const val VISIBLE_ITEM_COUNT = 3
        const val LOADING_FLAG_NUM = 3
    }
}