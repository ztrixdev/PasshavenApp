package ru.ztrixdev.projects.passhavenapp.Handlers

import android.content.Context
import ru.ztrixdev.projects.passhavenapp.Room.DatabaseProvider
import ru.ztrixdev.projects.passhavenapp.Room.Vault
import ru.ztrixdev.projects.passhavenapp.Room.VaultDao
import ru.ztrixdev.projects.passhavenapp.pHbeKt.AndroidCrypto
import ru.ztrixdev.projects.passhavenapp.pHbeKt.Checksum
import ru.ztrixdev.projects.passhavenapp.pHbeKt.CryptoNames
import ru.ztrixdev.projects.passhavenapp.pHbeKt.Keygen
import ru.ztrixdev.projects.passhavenapp.pHbeKt.MasterPassword
import java.security.Key
import java.security.KeyStore
import javax.crypto.SecretKey
import kotlin.uuid.Uuid

class VaultHandler {
    private val mpProtectingKeyName = "VaultKey_MP"
    private val mpHashProtectingKeyName = "VaultKey_MPHash"
    private val pinHashProtectingKeyName = "VaultKey_PINHash"

    private val keygen = Keygen
    private val androidCrypto = AndroidCrypto
    private val checksum = Checksum
    private val masterPassword = MasterPassword

    fun createVault(password: String, PIN: String, context: Context): List<Vault> {
        if (!masterPassword.verify(passwd = password))
            throw IllegalArgumentException("The provided password doesn't meet the requirements. Check the requirements and try again.")
        if (!masterPassword.verifyPIN(PIN))
            throw IllegalArgumentException("The provided PIN doesn't meet the requirements. Check the requirements and try again.")

        // Generates a new key in KeyStore to protect the MP-derived key.
        keygen.generateAndroidKey(mpProtectingKeyName)
        keygen.generateAndroidKey(mpHashProtectingKeyName)
        keygen.generateAndroidKey(pinHashProtectingKeyName)

        val keystore: KeyStore = KeyStore.getInstance(keygen.AKSProviderName).apply { load(null) }
        val mpProtectingKey: Key? = keystore.getKey(mpProtectingKeyName, null)
        val mpHashProtectingKey: Key? = keystore.getKey(mpHashProtectingKeyName, null)
        val pinHashProtectingKey: Key? = keystore.getKey(pinHashProtectingKeyName, null)
        if (mpProtectingKey == null)
            throw RuntimeException("Cannot retrieve the $mpProtectingKeyName key! A $mpProtectingKeyName key might not have been generated...")
        if (mpHashProtectingKey == null)
            throw RuntimeException("Cannot retrieve the $mpHashProtectingKeyName key! A $mpHashProtectingKeyName key might not have been generated...")
        if (pinHashProtectingKey == null)
            throw RuntimeException("Cannot retrieve the $pinHashProtectingKeyName key! A $pinHashProtectingKeyName key might nott have been generated...")

        val mpDerivedKeySaltPair = keygen.deriveKeySaltPairFromMP(password)
        val key = mpDerivedKeySaltPair[CryptoNames.key]
        val salt = mpDerivedKeySaltPair[CryptoNames.salt]
        val encryptedKeyCipherAndIV= androidCrypto.encrypt(key, mpProtectingKey as SecretKey)

        val mpHash = checksum.keccak512(password)
        val pinHash = checksum.keccak512(PIN)

        val encryptedMPHashCipherAndIV = androidCrypto.encrypt(mpHash, mpHashProtectingKey as SecretKey)
        val encryptedPINHashCipherAndIV = androidCrypto.encrypt(pinHash, pinHashProtectingKey as SecretKey)

        val vault = Vault(uuid = Uuid.random(),
            mpKey = encryptedKeyCipherAndIV[CryptoNames.cipher]!!,
            mpSalt = salt!!, mpIv = encryptedKeyCipherAndIV[CryptoNames.iv]!!,
            mpHash = encryptedMPHashCipherAndIV[CryptoNames.cipher]!!,
            mpHashIv = encryptedMPHashCipherAndIV[CryptoNames.iv]!!,
            pinHash = encryptedPINHashCipherAndIV[CryptoNames.cipher]!!,
            pinHashIv = encryptedPINHashCipherAndIV[CryptoNames.iv]!!,
            flabs = 20,
            flabsr = 20)

        val vaultDao = DatabaseProvider.getDatabase(context).vaultDao()
        // Deletes all the previous vaults to avoid having more than one vault, which will result in errors.
        if (!selfDestroy(vaultDao))
            throw RuntimeException("Cannot self-destroy (delete previous vaults). Maybe, an error occurred.")

        vaultDao.insert(vault)
        return vaultDao.getVault()
    }

    fun loginByPassword(passwd: String, context: Context): Boolean {
        val vaultDao = DatabaseProvider.getDatabase(context).vaultDao()
        val vault = vaultDao.getVault()
        if (vault == emptyList<Vault>())
            return false
        if (vault[0].flabsr <= 0)
            if (selfDestroy(vaultDao)) return false

        val keystore: KeyStore = KeyStore.getInstance(keygen.AKSProviderName).apply { load(null) }
        val mpHashProtectingKey: Key? = keystore.getKey(mpHashProtectingKeyName, null)
            ?: throw RuntimeException("Cannot retrieve the $mpHashProtectingKeyName key! A $mpHashProtectingKeyName key might not have been generated...")

        val decryptedMPHash = androidCrypto.decrypt(mapOf(CryptoNames.cipher to vault[0].mpHash, CryptoNames.iv to vault[0].mpHashIv), mpHashProtectingKey as SecretKey)

        val loginResult = checksum.keccak512(passwd).contentEquals(decryptedMPHash)
        if (!loginResult)
            vaultDao.update(flabsr = vault[0].flabsr - 1, uuid = vault[0].uuid)
        vaultDao.update(flabsr = vault[0].flabs, uuid = vault[0].uuid)

        return loginResult
    }

    fun loginByPIN(PIN: String, context: Context): Boolean {
        val vaultDao = DatabaseProvider.getDatabase(context).vaultDao()
        val vault = vaultDao.getVault()
        if (vault == emptyList<Vault>())
            return false
        if (vault[0].flabsr <= 0)
            if (selfDestroy(vaultDao)) return false

        val keystore: KeyStore = KeyStore.getInstance(keygen.AKSProviderName).apply { load(null) }
        val pinHashProtectingKey: Key? = keystore.getKey(pinHashProtectingKeyName, null)
            ?: throw RuntimeException("Cannot retrieve the $pinHashProtectingKeyName key! A $pinHashProtectingKeyName key might not have been generated...")

        val decryptedPINHash = androidCrypto.decrypt(mapOf(CryptoNames.cipher to vault[0].pinHash, CryptoNames.iv to vault[0].pinHashIv), pinHashProtectingKey as SecretKey)
        val loginResult = checksum.keccak512(PIN.toString()).contentEquals(decryptedPINHash)
        if (!loginResult)
            vaultDao.update(flabsr = vault[0].flabsr - 1, uuid = vault[0].uuid)
        vaultDao.update(flabsr = vault[0].flabs, uuid = vault[0].uuid)

        return loginResult
    }

    fun selfDestroy(dao: VaultDao): Boolean {
        var vaults = dao.getVault()
        while (vaults != emptyList<Vault>()) {
            for (vlt: Vault in vaults) {
                dao.delete(vlt)
            }
            vaults = dao.getVault()
        }
        return true
    }
}
