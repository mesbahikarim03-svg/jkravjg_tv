package com.example.universaldownloader

import android.util.Patterns

object UrlHelper {

    fun isValidUrl(url: String): Boolean {
        return Patterns.WEB_URL.matcher(url).matches()
    }

    fun guessFileName(url: String): String {
        val base = url.substringAfterLast("/").substringBefore("?")
        return if (base.isBlank()) "downloaded_file" else base
    }
}