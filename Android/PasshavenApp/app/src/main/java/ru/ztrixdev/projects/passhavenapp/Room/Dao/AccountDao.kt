package ru.ztrixdev.projects.passhavenapp.Room.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ru.ztrixdev.projects.passhavenapp.Room.Account
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Dao
@OptIn(ExperimentalUuidApi::class)
interface AccountDao {
    @Query("select * from account")
    suspend fun getALl(): List<Account>

    @Query("select * from account where uuid like :accountUuid limit 1")
    suspend fun getAccountByUuid(accountUuid: Uuid): Account?

    @Insert
    suspend fun insert(vararg acc: Account)

    @Update
    suspend fun update(vararg acc: Account)

    @Delete
    suspend fun delete(vararg acc: Account)
}