package com.juniori.puzzle.util

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri

class VideoMetaDataUtil(private val context: Context) {

    fun getVideoDurationInSeconds(videoUri: Uri): Long? {
        return MediaMetadataRetriever().use { retriever ->
            retriever.setDataSource(context, videoUri)
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.let { milliseconds: String ->
                    milliseconds.toLong() / 1000
                }
        }
    }

    fun extractThumbnail(videoFilePath: String): ByteArray? {
        return MediaMetadataRetriever().use { retriever ->
            retriever.setDataSource(context, Uri.parse(videoFilePath))
            retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                ?.compressToBytes(Bitmap.CompressFormat.JPEG, 100)
        }
    }
}
