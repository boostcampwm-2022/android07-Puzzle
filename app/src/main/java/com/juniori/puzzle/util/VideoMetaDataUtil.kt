package com.juniori.puzzle.util

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever

object VideoMetaDataUtil {

    fun getVideoDurationInSeconds(videoFilePath: String): Long? {
        return MediaMetadataRetriever().run {
            setDataSource(videoFilePath)
            extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.let { milliseconds: String ->
                    this.release()
                    milliseconds.toLong() / 1000
                }
        }
    }

    fun extractThumbnail(videoFilePath: String): ByteArray? {
        return MediaMetadataRetriever().run {
            setDataSource(videoFilePath)
            getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)?.let { bitmap ->
                this.release()
                bitmap.compressToBytes(Bitmap.CompressFormat.JPEG, 100)
            }
        }
    }
}
