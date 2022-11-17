package com.juniori.puzzle.adapter

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

@BindingAdapter("setImage")
fun setImage(view: ImageView, url: String){
    //todo glide
}

@BindingAdapter("app:setAdapter")
fun <T> setAdapter(view: RecyclerView, itemList: List<T>) {
    val adapter = view.adapter as ListAdapter<T, RecyclerView.ViewHolder>
    adapter.submitList(itemList)
}

