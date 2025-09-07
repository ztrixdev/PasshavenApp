package ru.ztrixdev.projects.passhavenapp.Room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.ztrixdev.projects.passhavenapp.pHbeKt.SodiumCrypto
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity
@OptIn(ExperimentalUuidApi::class)
data class Account (
    @PrimaryKey val uuid: Uuid,
    @ColumnInfo(name = "reprompt") var reprompt: Boolean,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "username") var username: String,
    @ColumnInfo(name = "password") var password: String,
    @ColumnInfo(name = "mfa_secret") var mfaSecret: String?,
    @ColumnInfo(name = "recovery_codes") var recoveryCodes: List<String>?,
    @ColumnInfo(name = "additional_note") var additionalNote: String?
)

fun Account.encrypt(key: ByteArray) {
    name = SodiumCrypto.encrypt(name, key)
    username = SodiumCrypto.encrypt(username, key)
    password = SodiumCrypto.encrypt(password, key)
    if (mfaSecret != null)
        mfaSecret = SodiumCrypto.encrypt(mfaSecret as String, key)
    if (recoveryCodes != null) {
        if (recoveryCodes != emptyList<String>()) {
            val encryptedCodes = emptyList<String>().toMutableList()
            for (code in recoveryCodes) {
                encryptedCodes += SodiumCrypto.encrypt(code, key)
            }
            recoveryCodes = encryptedCodes
        }
    }
    if (additionalNote != null)
        additionalNote = SodiumCrypto.encrypt(additionalNote as String, key)
}

fun Account.decrypt(key: ByteArray) {
    name = SodiumCrypto.decrypt(name, key)
    username = SodiumCrypto.decrypt(username, key)
    password = SodiumCrypto.decrypt(password, key)
    if (mfaSecret != null)
        mfaSecret = SodiumCrypto.decrypt(mfaSecret as String, key)
    if (recoveryCodes != null) {
        if (recoveryCodes != emptyList<String>()) {
            val decryptedCodes = emptyList<String>().toMutableList()
            for (code in recoveryCodes) {
                decryptedCodes += SodiumCrypto.decrypt(code, key)
            }
            recoveryCodes = decryptedCodes
        }
    }
    if (additionalNote != null)
        additionalNote = SodiumCrypto.decrypt(additionalNote as String, key)
}

