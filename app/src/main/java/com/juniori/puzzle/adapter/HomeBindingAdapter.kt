package com.juniori.puzzle.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.juniori.puzzle.R

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