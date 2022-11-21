package com.juniori.puzzle.util

import android.location.Address
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

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

fun Address.toAddressString():String{
    return if (locality == null) {
        "$adminArea $thoroughfare"
    } else {
        "$adminArea $locality $thoroughfare"
    }
}