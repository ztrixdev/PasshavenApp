package ru.ztrixdev.projects.passhavenapp.Room.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ru.ztrixdev.projects.passhavenapp.Room.Card
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Dao
@OptIn(ExperimentalUuidApi::class)
interface CardDao {
    @Query("select * from card")
    suspend fun getALl(): List<Card>

    @Query("select * from card where uuid like :cardUuid limit 1")
    suspend fun getCardByUuid(cardUuid: Uuid): Card?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg crd: Card)

    @Update
    suspend fun update(vararg crd: Card)

    @Delete
    suspend fun delete(vararg crd: Card)
}
