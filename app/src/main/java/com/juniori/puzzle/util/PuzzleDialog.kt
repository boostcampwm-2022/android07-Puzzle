package com.juniori.puzzle.util

import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.juniori.puzzle.R
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class PuzzleDialog @Inject constructor(
    @ActivityContext private val context: Context
) {
    private var dialog = MaterialAlertDialogBuilder(context, R.style.Theme_Puzzle_Dialog)
    private val listPopupWindow = ListPopupWindow(
        context,
        null,
        com.google.android.material.R.attr.listPopupWindowStyle
    )

    fun buildAlertDialog(yesCallback: () -> Unit, noCallback: () -> Unit): PuzzleDialog {
        dialog.setPositiveButton(R.string.all_yes) { _, _ ->
            yesCallback()
        }.setNegativeButton(R.string.all_no) { _, _ ->
            noCallback()
        }.also { dialog = it }
        return this
    }

    fun buildConfirmationDialog(
        confirmCallback: () -> Unit,
        cancelCallback: () -> Unit
    ): PuzzleDialog {
        dialog.setPositiveButton(R.string.all_accept) { _, _ ->
            confirmCallback()
        }.setNegativeButton(R.string.all_cancel) { _, _ ->
            cancelCallback()
        }.also { dialog = it }
        return this
    }

    fun buildListPopup(anchorView: View, items:Array<String>):PuzzleDialog{
        listPopupWindow.anchorView = anchorView

        val spinnerAdapter =
            ArrayAdapter(context, R.layout.item_popup_list, items)
        listPopupWindow.setAdapter(spinnerAdapter)

        return this
    }

    fun setListPopupItemListener(listener:android.widget.AdapterView.OnItemClickListener){
        listPopupWindow.setOnItemClickListener(listener)
    }

    fun setMessage(message: String): PuzzleDialog {
        dialog.setMessage(message).also { dialog = it }
        return this
    }

    fun setTitle(title: String): PuzzleDialog {
        dialog.setTitle(title).also { dialog = it }
        return this
    }

    fun showDialog(): androidx.appcompat.app.AlertDialog = dialog.show()

    fun showPopupList() = listPopupWindow.show()
    fun dismissPopupList() = listPopupWindow.dismiss()
}