package ru.ztrixdev.projects.passhavenapp.room.dataModels

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity
@OptIn(ExperimentalUuidApi::class)
data class Vault (
    @PrimaryKey val uuid: Uuid,
    @ColumnInfo("mp_key") var mpKey: ByteArray,
    @ColumnInfo("mp_salt") var mpSalt: ByteArray,
    @ColumnInfo("mp_iv") var mpIv: ByteArray,
    @ColumnInfo("mp_hash") var mpHash: ByteArray,
    @ColumnInfo("mp_hash_iv") var mpHashIv: ByteArray,
    @ColumnInfo("pin_hash") var pinHash: ByteArray,
    @ColumnInfo("pin_hash_iv") var pinHashIv: ByteArray,
)
