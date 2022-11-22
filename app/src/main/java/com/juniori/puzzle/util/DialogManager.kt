package com.juniori.puzzle.util

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View

class DialogManager constructor(
    context: Context
) {
    private val builder = AlertDialog.Builder(context)
    private var dialog: AlertDialog? = null

    fun createLoadingDialog(view: View) {
        dialog = builder.setView(view).create().apply{
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    fun showDialog() {
        dialog ?: return
        dialog?.show()
    }

    fun dismissDialog() {
        dialog ?: return
        dialog?.dismiss()
    }
}