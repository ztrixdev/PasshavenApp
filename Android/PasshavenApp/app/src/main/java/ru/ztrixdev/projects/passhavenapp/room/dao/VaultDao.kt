package ru.ztrixdev.projects.passhavenapp.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ru.ztrixdev.projects.passhavenapp.room.Vault
import kotlin.uuid.ExperimentalUuidApi

@Dao
@OptIn(ExperimentalUuidApi::class)
interface VaultDao {
    @Query("select * from vault")
    suspend fun getVault(): List<Vault>

    @Insert
    suspend fun insert(vararg vlt: Vault)

    @Update
    suspend fun update(vararg vlt: Vault)

    @Delete
    suspend fun delete(vararg vlt: Vault)
}
