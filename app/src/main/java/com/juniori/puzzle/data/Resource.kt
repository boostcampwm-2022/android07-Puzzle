package com.juniori.puzzle.data

sealed class Resource<out R> {
    data class Success<out R>(val result: R) : Resource<R>()
    data class Failure(val exception: Exception) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
    object Empty: Resource<Nothing>()
    object Wait: Resource<Nothing>()
}
