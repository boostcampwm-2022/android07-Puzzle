package com.juniori.puzzle.util

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

fun ByteArray.saveInFile(filePath: String): Unit? {
    return try {
        val file = File(filePath)
        FileOutputStream(file).use { fileOutputStream ->
            fileOutputStream.write(this)
        }
    } catch (e: Exception) {
        null
    }
}

fun Uri.readBytes(contentResolver: ContentResolver): ByteArray? {
    return try {
        contentResolver.openInputStream(this)?.use { inputStream ->
            inputStream.readBytes()
        }
    } catch (e: Exception) {
        null
    }
}

fun Bitmap.compressToBytes(format: Bitmap.CompressFormat, quality: Int): ByteArray? {
    return try {
        ByteArrayOutputStream().use { outputStream ->
            val compressed = this.compress(format, quality, outputStream)
            if (compressed) outputStream.toByteArray() else null
        }
    } catch (e: Exception) {
        null
    }
}

fun String.deleteIfFileUri(): Boolean =
    try {
        File(this).let { file ->
            if (file.exists()) file.delete() else false
        }
    } catch (e: Exception) {
        false
    }
