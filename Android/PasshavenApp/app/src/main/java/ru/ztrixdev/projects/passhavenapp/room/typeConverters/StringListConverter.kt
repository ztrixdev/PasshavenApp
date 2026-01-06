package ru.ztrixdev.projects.passhavenapp.room.typeConverters

import androidx.room.TypeConverter
import com.google.gson.Gson

class StringListConverter {
    private val gson = Gson()

    @TypeConverter
    fun listToJson(list: List<String>): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun jsonToList(json: String): List<String> {
        return gson.fromJson(json, Array<String>::class.java).toList()
    }
}


