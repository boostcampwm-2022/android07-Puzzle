package com.juniori.puzzle.util

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import com.juniori.puzzle.databinding.DialogProgressBinding

class ProgressDialog(context: Context) : Dialog(context) {
    val binding = DialogProgressBinding.inflate(LayoutInflater.from(context))

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        setCancelable(false)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    @SuppressLint("SetTextI18n")
    fun setProgress(progress: Int) {
        binding.progressText.text = "$progress%"
        binding.progressBar.setProgress(progress, true)
    }
}