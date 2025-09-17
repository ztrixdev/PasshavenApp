package ru.ztrixdev.projects.passhavenapp.Room.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ru.ztrixdev.projects.passhavenapp.Room.Card
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Dao
@OptIn(ExperimentalUuidApi::class)
interface CardDao {
    @Query("select * from card")
    fun getALl(): List<Card>

    @Query("select * from card where uuid like :cardUuid limit 1")
    fun getCardByUuid(cardUuid: Uuid): Card?

    @Insert
    fun insert(vararg crd: Card)

    @Update
    fun update(vararg crd: Card)

    @Delete
    fun delete(vararg crd: Card)
}
