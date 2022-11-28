package com.juniori.puzzle.adapter

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.juniori.puzzle.R
import java.util.*

private val calendar = Calendar.getInstance()

@BindingAdapter("setImage")
fun setImage(view: ImageView, url: String?) {
    if (url.isNullOrEmpty()) return

    Glide.with(view.context)
        .load(url)
        .into(view)
}

@BindingAdapter("setAdapter")
fun <T> setAdapter(view: RecyclerView, itemList: List<T>) {
    val adapter = view.adapter as ListAdapter<T, RecyclerView.ViewHolder>
    adapter.submitList(itemList)
}

@BindingAdapter("setFullDate")
fun setFullDate(view: TextView, date: Date) {
    calendar.time = date

    view.text = String.format(
        view.context.getString(R.string.full_date_format),
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.DATE),
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE)
    )
}

@BindingAdapter("setTime")
fun setTime(view: TextView, date: Date) {
    calendar.time = date
    val amText = view.context.getString(R.string.time_AM_date_format)
    val pmText = view.context.getString(R.string.time_PM_date_format)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    view.text = if (hour > 12) {
        String.format(pmText, hour - 12)
    } else if (hour == 12) {
        String.format(pmText, hour)
    } else {
        String.format(amText, hour)
    }
}

@BindingAdapter("setNickname")
fun setNickname(view: TextView, nickname: String?) {
    val displayName = nickname?.let {
        "${it}님"
    } ?: run { "${view.context.getString(R.string.display_anonymous)}님" }

    view.text = displayName
}


