package ru.ztrixdev.projects.passhavenapp.Handlers

import android.content.Context
import androidx.core.util.Consumer
import ru.ztrixdev.projects.passhavenapp.Room.DatabaseProvider
import ru.ztrixdev.projects.passhavenapp.Room.Vault
import ru.ztrixdev.projects.passhavenapp.pHbeKt.AndroidCrypto
import ru.ztrixdev.projects.passhavenapp.pHbeKt.Checksum
import ru.ztrixdev.projects.passhavenapp.pHbeKt.CryptoNames
import ru.ztrixdev.projects.passhavenapp.pHbeKt.Keygen
import java.security.Key
import java.security.KeyStore
import javax.crypto.SecretKey
import kotlin.uuid.Uuid

class VaultHandler {
    private val mpProtectingKeyName = "VaultKey_MP"
    private val kg: Keygen = Keygen()
    private val ac: AndroidCrypto = AndroidCrypto()
    private val cs: Checksum = Checksum()

    fun createVault(masterPassword: String, PIN: Int, context: Context): List<Vault> {
        // Generates a new key in KeyStore to protect the MP-derived key.
        kg.generateAndroidKey(mpProtectingKeyName)

        val keystore: KeyStore = KeyStore.getInstance(kg.AKSProviderName).apply { load(null) }
        val mpProtectingKey: Key? = keystore.getKey(mpProtectingKeyName, null)
        if (mpProtectingKey == null)
            throw RuntimeException("Cannot retrieve the $mpProtectingKeyName key! A $mpProtectingKeyName key might not have been generated...")

        val mpDerivedKeySaltPair = kg.deriveKeySaltPairFromMP(masterPassword)
        val key = mpDerivedKeySaltPair[CryptoNames.key]
        val salt = mpDerivedKeySaltPair[CryptoNames.salt]
        val encryptedKeyCipherAndIV= ac.encrypt(key, mpProtectingKey as SecretKey)

        val vault = Vault(uuid = Uuid.random(),
            mpKey = encryptedKeyCipherAndIV[CryptoNames.cipher]!!,
            mpSalt = salt!!, mpIv = encryptedKeyCipherAndIV[CryptoNames.iv]!!,
            mpHash = cs.keccak512(masterPassword),
            pinHash = cs.keccak512(PIN.toString()))

        val vaultDao = DatabaseProvider.getDatabase(context).vaultDao()
        vaultDao.insert(vault)
        return vaultDao.getVault()
    }

    fun loginByPassword(passwd: String, context: Context): Boolean {
        val vaultDao = DatabaseProvider.getDatabase(context).vaultDao()
        val vault = vaultDao.getVault()
        if (vault == emptyList<Vault>())
            return false
        if (!cs.keccak512(passwd).contentEquals(vault[0].mpHash))
            return false
        return true
    }

    fun loginByPIN(PIN: Int, context: Context): Boolean {
        val vaultDao = DatabaseProvider.getDatabase(context).vaultDao()
        val vault = vaultDao.getVault()
        if (vault == emptyList<Vault>())
            return false
        if (!cs.keccak512(PIN.toString()).contentEquals(vault[0].mpHash))
            return false
        return true
    }
}