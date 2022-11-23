package com.juniori.puzzle.ui.addvideo

import android.net.Uri

sealed class AddVideoActionState {

    data class VideoPicked(val uri: Uri, val videoBytes: ByteArray) : AddVideoActionState()
    data class TakingVideoCompleted(val videoName: String) : AddVideoActionState()
}
