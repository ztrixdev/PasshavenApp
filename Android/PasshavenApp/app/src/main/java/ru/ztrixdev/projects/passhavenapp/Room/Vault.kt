package ru.ztrixdev.projects.passhavenapp.Room

import android.net.Uri
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
    @ColumnInfo("failed_login_attempts_before_suicide") var flabs: Int,
    @ColumnInfo("failed_login_attempts_before_suicide_remaining") var flabsr: Int,
    @ColumnInfo("backup_folder") var backupFolder: Uri,
    @ColumnInfo("last_backup") var lastBackup: Long,
    @ColumnInfo("backup_every") var backupEvery: Long
)
