package ru.ztrixdev.projects.passhavenapp.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ru.ztrixdev.projects.passhavenapp.room.dataModels.Account
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Dao
@OptIn(ExperimentalUuidApi::class)
interface AccountDao {
    @Query("select * from account")
    suspend fun getALl(): List<Account>

    @Query("select * from account where uuid like :accountUuid limit 1")
    suspend fun getAccountByUuid(accountUuid: Uuid): Account?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg acc: Account)

    @Update
    suspend fun update(vararg acc: Account)

    @Delete
    suspend fun delete(vararg acc: Account)
}