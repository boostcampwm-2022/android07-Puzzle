package com.juniori.puzzle.ui.mygallery

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.juniori.puzzle.databinding.ItemGalleryRecyclerBinding


class MyGalleryAdapter(val viewModel: MyGalleryViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var dataList = listOf<VideoMockData>()

    class ViewHolder(val binding: ItemGalleryRecyclerBinding, val height: Int) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VideoMockData) {
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
        if(position==itemCount-1) {
            viewModel.getData(itemCount)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<VideoMockData>){
        dataList = list
        notifyDataSetChanged()//todo notify -> diffutil
    }
}