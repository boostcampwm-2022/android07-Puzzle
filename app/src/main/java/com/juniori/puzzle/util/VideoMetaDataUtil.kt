package com.juniori.puzzle.util

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever

object VideoMetaDataUtil {

    fun getVideoDurationInSeconds(videoFilePath: String): Long? {
        return MediaMetadataRetriever().use { retriever ->
            retriever.setDataSource(videoFilePath)
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.let { milliseconds: String ->
                    milliseconds.toLong() / 1000
                }
        }
    }

    fun extractThumbnail(videoFilePath: String): ByteArray? {
        return MediaMetadataRetriever().use { retriever ->
            retriever.setDataSource(videoFilePath)
            retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                ?.compressToBytes(Bitmap.CompressFormat.JPEG, 100)
        }
    }
}
