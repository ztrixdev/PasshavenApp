package ru.ztrixdev.projects.passhavenapp.Room.TypeConverters

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.TypeConverter

class UriConverter {
    @TypeConverter
    fun uriToString(uri: Uri): String? {
        return uri.toString()
    }

    @TypeConverter
    fun stringToUri(string: String): Uri? {
        return string.toUri()
    }
}