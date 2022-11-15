package com.juniori.puzzle.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("app:setText")
fun setText(view: TextView, text: String) {
    view.text = text
}