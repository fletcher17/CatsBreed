package com.example.catsbreed.util

import java.io.IOException

class NoConnectivityException(
    message: String = "No internet connection available.",
    cause: Throwable? = null
) : IOException(message, cause)