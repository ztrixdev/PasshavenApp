package io.github.ztrixdev.passhavenapp.Room

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
data class Account (
    @PrimaryKey val uuid: Uuid,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "is_second_layer_protected") val isSLPd: Boolean,
    @ColumnInfo(name = "mp_reprompt") val mpReprompt: Boolean?,
    @ColumnInfo(name = "second_layer_salt") val SLS: String?,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "mfa_secret") val mfaSecret: String?,
    @ColumnInfo(name = "passkey") val passkey: String?,
    @ColumnInfo(name = "recovery_codes") val recoveryCodes: List<String>?,
    @ColumnInfo(name = "additional_note") val additionalNote: String?
)

@Dao
@OptIn(ExperimentalUuidApi::class)
interface AccountDao {
    @Query("select * from account")
    fun getALl(): List<Account>

    @Query("select * from account where uuid like :accountUUID limit 1")
    fun getFolderByUUID(accountUUID: Uuid): Account

    @Insert
    fun insert(vararg acc: Account)

    @Update
    fun update(vararg acc: Account)

    @Delete
    fun delete(vararg acc: Account)
}