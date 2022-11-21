package com.juniori.puzzle.util

import android.annotation.SuppressLint
import android.location.Address
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resumeWithException

@SuppressLint("SimpleDateFormat")
private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> Task<T>.await(): T {
    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener {
            it.exception?.let { exception ->
                cont.resumeWithException(exception)
            } ?: cont.resume(it.result, null)
        }
    }
}

fun Address.toAddressString(): String {
    return if (locality == null) {
        "$adminArea $thoroughfare"
    } else {
        "$adminArea $locality $thoroughfare"
    }
}

fun String.toDate(): Date = formatter.parse(this) ?: Date()