package ru.ztrixdev.projects.passhavenapp.Room.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ru.ztrixdev.projects.passhavenapp.Room.Vault
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Dao
@OptIn(ExperimentalUuidApi::class)
interface VaultDao {
    @Query("select * from vault")
    suspend fun getVault(): List<Vault>

    @Insert
    suspend fun insert(vararg vlt: Vault)

    @Query("update vault set failed_login_attempts_before_suicide_remaining=:flabsr where uuid=:uuid")
    suspend fun update(flabsr: Int, uuid: Uuid)

    @Update
    suspend fun update(vararg vlt: Vault)

    @Delete
    suspend fun delete(vararg vlt: Vault)
}
