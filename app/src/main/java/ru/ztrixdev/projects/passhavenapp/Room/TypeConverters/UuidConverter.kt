package ru.ztrixdev.projects.passhavenapp.Room.TypeConverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class UuidConverter {
    private val gson = Gson()

    @TypeConverter
    fun uuidToString(uuid: Uuid): String {
        return uuid.toString()
    }

    @TypeConverter
    fun stringToUuid(string: String): Uuid {
        return Uuid.parse(string)
    }

    @TypeConverter
    fun uuidListToJson(uuidList: List<Uuid>?): String? {
        if (uuidList == null) {
            return null
        }
        val stringList = uuidList.map { it.toString() }
        return gson.toJson(stringList)
    }

    @TypeConverter
    fun jsonToUuidList(jsonString: String?): List<Uuid> {
        if (jsonString.isNullOrEmpty()) {
            return emptyList()
        }
        val type = object : TypeToken<List<String>>() {}.type
        val stringList: List<String>? = gson.fromJson(jsonString, type)

        return stringList?.mapNotNull { str ->
            try {
                Uuid.parse(str)
            } catch (e: IllegalArgumentException) {
                null
            }
        } ?: emptyList()
    }
}