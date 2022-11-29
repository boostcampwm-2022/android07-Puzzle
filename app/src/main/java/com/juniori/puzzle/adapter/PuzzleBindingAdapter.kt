package com.juniori.puzzle.adapter

import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.button.MaterialButton
import com.juniori.puzzle.R
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import java.util.*

private val calendar = Calendar.getInstance()

private const val DRAWABLE_WIDTH = 120
private const val DRAWABLE_HEIGHT = 120

@BindingAdapter("setImage")
fun setImage(view: ImageView, url: String?) {
    if (url.isNullOrEmpty()) return

    Glide.with(view.context)
        .load(url)
        .into(view)
}

@BindingAdapter("setDrawableLeft")
fun setDrawableLeft(view: TextView, url: String?) {
    if (url.isNullOrEmpty()) return

    try {
        val resourceId = url.toInt()
        Glide.with(view.context)
            .load(resourceId)
            .into(object : CustomTarget<Drawable>(DRAWABLE_WIDTH, DRAWABLE_HEIGHT) {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    resource.setBounds(0, 0, DRAWABLE_WIDTH, DRAWABLE_HEIGHT)
                    view.setCompoundDrawables(resource, null, null, null)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    placeholder?.setBounds(0, 0, DRAWABLE_WIDTH, DRAWABLE_HEIGHT)
                    view.setCompoundDrawablesWithIntrinsicBounds(placeholder, null, null, null)
                }
            })
    } catch (e: Exception) {
        Glide.with(view.context)
            .load(url)
            .into(object : CustomTarget<Drawable>(DRAWABLE_WIDTH, DRAWABLE_HEIGHT) {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    resource.setBounds(0, 0, DRAWABLE_WIDTH, DRAWABLE_HEIGHT)
                    view.setCompoundDrawables(resource, null, null, null)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    placeholder?.setBounds(0, 0, DRAWABLE_WIDTH, DRAWABLE_HEIGHT)
                    view.setCompoundDrawablesWithIntrinsicBounds(placeholder, null, null, null)
                }
            })
    }

}

@BindingAdapter("setLikeCount")
fun setLikeCount(view: MaterialButton, updateFlow: Resource<VideoInfoEntity>?) {
    if (updateFlow is Resource.Success) {
        view.text = updateFlow.result.likedCount.toString()
    }
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


