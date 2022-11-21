package com.juniori.puzzle.util

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri

object VideoMetaDataUtil {

    fun getVideoDurationInSeconds(context: Context, videoUri: Uri): Long? {
        return MediaMetadataRetriever().use { retriever ->
            retriever.setDataSource(context, videoUri)
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.let { milliseconds: String ->
                    milliseconds.toLong() / 1000
                }
        }
    }
}
