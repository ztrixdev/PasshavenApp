package ru.ztrixdev.projects.passhavenapp.Room

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity
@OptIn(ExperimentalUuidApi::class)
data class Vault (
    @PrimaryKey val uuid: Uuid,
    @ColumnInfo("mp_key") val mpKey: ByteArray,
    @ColumnInfo("mp_salt") val mpSalt: ByteArray,
    @ColumnInfo("mp_iv") val mpIv: ByteArray,
    @ColumnInfo("mp_hash") val mpHash: ByteArray,
    @ColumnInfo("pin_hash") val pinHash: ByteArray
)

@Dao
@OptIn(ExperimentalUuidApi::class)
interface VaultDao {
    @Query("select * from vault")
    fun getVault(): List<Vault>

    @Insert
    fun insert(vararg vlt: Vault)

    @Update
    fun update(vararg vlt: Vault)

    @Delete
    fun delete(vararg vlt: Vault)
}

