package com.juniori.puzzle.util

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

// TODO exception 처리
fun ByteArray.saveInFile(filePath: String) {
    val file = File(filePath)
    FileOutputStream(file).use { fileOutputStream ->
        fileOutputStream.write(this)
    }
}

fun Uri.readBytes(contentResolver: ContentResolver): ByteArray? {
    return contentResolver.openInputStream(this)?.use { inputStream ->
        inputStream.readBytes()
    }
}

fun Bitmap.compressToBytes(format: Bitmap.CompressFormat, quality: Int): ByteArray {
    return ByteArrayOutputStream().use { outputStream ->
        this.compress(format, quality, outputStream)
        outputStream.toByteArray()
    }
}
