package com.juniori.puzzle.adapter

import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.juniori.puzzle.R
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import java.util.Calendar
import java.util.Date

private val calendar = Calendar.getInstance()

@BindingAdapter("setAdapter")
fun <T> setAdapter(view: RecyclerView, itemList: List<T>) {
    val adapter = view.adapter as ListAdapter<T, RecyclerView.ViewHolder>
    adapter.submitList(itemList)
}

@BindingAdapter("setLikeCount")
fun setLikeCount(view: MaterialButton, updateFlow: Resource<VideoInfoEntity>?) {
    if (updateFlow is Resource.Success) {
        view.text = updateFlow.result.likedCount.toString()
    }
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

@BindingAdapter("setDisplayName")
fun setDisplayName(view: TextView, name: String) {
    view.text = if (name.isNotEmpty()) {
        String.format(view.context.getString(R.string.display_name_format), name)
    } else {
        String.format(
            view.context.getString(R.string.display_name_format),
            view.context.getString(R.string.display_anonymous)
        )
    }
}



