package com.juniori.puzzle.util

import android.content.ContentResolver
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

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
