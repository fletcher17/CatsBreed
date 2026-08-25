package com.example.catsbreed.util

import java.io.IOException

class NoConnectivityException(
    message: String = "No internet connection available.",
    cause: Throwable? = null
) : IOException(message, cause)

fun Throwable.toUserMessage(): String = when (this) {
    is NoConnectivityException -> "No internet connection."
    else -> message ?: "Something went wrong. Please try again."
}