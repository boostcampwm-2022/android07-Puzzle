package com.juniori.puzzle.util

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.juniori.puzzle.R
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class PuzzleDialog @Inject constructor(
    @ActivityContext private val context: Context
) {
    private var dialog = MaterialAlertDialogBuilder(context, R.style.Theme_Puzzle_Dialog)

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

    fun setMessage(message: String): PuzzleDialog {
        dialog.setMessage(message).also { dialog = it }
        return this
    }

    fun setTitle(title: String): PuzzleDialog {
        dialog.setTitle(title).also { dialog = it }
        return this
    }

    fun show(): androidx.appcompat.app.AlertDialog = dialog.show()
}