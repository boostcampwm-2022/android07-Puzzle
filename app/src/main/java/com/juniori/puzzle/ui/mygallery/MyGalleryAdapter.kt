package com.juniori.puzzle.ui.mygallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.juniori.puzzle.databinding.ItemGalleryRecyclerBinding
import com.juniori.puzzle.domain.entity.VideoInfoEntity


class MyGalleryAdapter(val viewModel: MyGalleryViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var dataList = mutableListOf<VideoInfoEntity>()

    class ViewHolder(val binding: ItemGalleryRecyclerBinding, val height: Int) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VideoInfoEntity) {
            binding.root.layoutParams.height = height/3
            binding.data = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemGalleryRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding, parent.height)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    private fun calDiff(newData: List<VideoInfoEntity>) {
        val diffCallBack = GalleryDiffCallBack(dataList, newData)
        val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(diffCallBack)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setData(newData: List<VideoInfoEntity>) {
        calDiff(newData)
        dataList.clear()
        dataList.addAll(newData)
    }
}