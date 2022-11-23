package com.juniori.puzzle.util

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.juniori.puzzle.R
import com.juniori.puzzle.databinding.NetworkFailLayoutBinding

class StateManager constructor(
    private val context: Context
) {
    private val builder = AlertDialog.Builder(context)
    private var dialog: AlertDialog? = null
    private var networkParent: ViewGroup? = null
    private var networkView: NetworkFailLayoutBinding? = null

    fun createLoadingDialog(parent: ViewGroup?) {
        val view = LayoutInflater.from(context).inflate(R.layout.loading_layout, parent, false)
        dialog = builder.setView(view).create().apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    fun showLoadingDialog() {
        dialog ?: return
        dialog?.show()
    }

    fun dismissLoadingDialog() {
        dialog ?: return
        dialog?.dismiss()
    }

    fun showNetworkDialog(parent: ViewGroup, msg: String) {
        networkView =
            NetworkFailLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, true)
                .apply {
                    infoText.text = msg
                }
        networkParent = parent
    }

    fun removeNetworkDialog() {
        networkParent ?: return

        networkParent?.removeView(networkView?.root)
    }
}