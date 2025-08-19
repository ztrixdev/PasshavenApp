package ru.ztrixdev.projects.passhavenapp.Handlers

import android.content.Context
import androidx.core.util.Consumer
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

    private val kg: Keygen = Keygen()
    private val ac: AndroidCrypto = AndroidCrypto()
    private val cs: Checksum = Checksum()
    private val mp: MasterPassword = MasterPassword()

    fun createVault(masterPassword: String, PIN: Int, context: Context): List<Vault> {
        if (!mp.verify(passwd = masterPassword))
            throw IllegalArgumentException("The provided password doesn't meet the requirements. Check the requirements and try again.")
        if (!mp.verifyPIN(PIN))
            throw IllegalArgumentException("The provided PIN doesn't meet the requirements. Check the requirements and try again.")

        // Generates a new key in KeyStore to protect the MP-derived key.
        kg.generateAndroidKey(mpProtectingKeyName)
        kg.generateAndroidKey(mpHashProtectingKeyName)
        kg.generateAndroidKey(pinHashProtectingKeyName)

        val keystore: KeyStore = KeyStore.getInstance(kg.AKSProviderName).apply { load(null) }
        val mpProtectingKey: Key? = keystore.getKey(mpProtectingKeyName, null)
        val mpHashProtectingKey: Key? = keystore.getKey(mpHashProtectingKeyName, null)
        val pinHashProtectingKey: Key? = keystore.getKey(pinHashProtectingKeyName, null)
        if (mpProtectingKey == null)
            throw RuntimeException("Cannot retrieve the $mpProtectingKeyName key! A $mpProtectingKeyName key might not have been generated...")
        if (mpHashProtectingKey == null)
            throw RuntimeException("Cannot retrieve the $mpHashProtectingKeyName key! A $mpHashProtectingKeyName key might not have been generated...")
        if (pinHashProtectingKey == null)
            throw RuntimeException("Cannot retrieve the $pinHashProtectingKeyName key! A $pinHashProtectingKeyName key might nott have been generated...")

        val mpDerivedKeySaltPair = kg.deriveKeySaltPairFromMP(masterPassword)
        val key = mpDerivedKeySaltPair[CryptoNames.key]
        val salt = mpDerivedKeySaltPair[CryptoNames.salt]
        val encryptedKeyCipherAndIV= ac.encrypt(key, mpProtectingKey as SecretKey)

        val mpHash = cs.keccak512(masterPassword)
        val pinHash = cs.keccak512(PIN.toString())

        val encryptedMPHashCipherAndIV = ac.encrypt(mpHash, mpHashProtectingKey as SecretKey)
        val encryptedPINHashCipherAndIV = ac.encrypt(pinHash, pinHashProtectingKey as SecretKey)

        val vault = Vault(uuid = Uuid.random(),
            mpKey = encryptedKeyCipherAndIV[CryptoNames.cipher]!!,
            mpSalt = salt!!, mpIv = encryptedKeyCipherAndIV[CryptoNames.iv]!!,
            mpHash = encryptedMPHashCipherAndIV[CryptoNames.cipher]!!,
            mpHashIv = encryptedMPHashCipherAndIV[CryptoNames.iv]!!,
            pinHash = encryptedPINHashCipherAndIV[CryptoNames.cipher]!!,
            pinHashIv = encryptedPINHashCipherAndIV[CryptoNames.iv]!!)

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

        val keystore: KeyStore = KeyStore.getInstance(kg.AKSProviderName).apply { load(null) }
        val mpHashProtectingKey: Key? = keystore.getKey(mpHashProtectingKeyName, null)
            ?: throw RuntimeException("Cannot retrieve the $mpHashProtectingKeyName key! A $mpHashProtectingKeyName key might not have been generated...")

        val decryptedMPHash = ac.decrypt(mapOf(CryptoNames.cipher to vault[0].mpHash, CryptoNames.iv to vault[0].mpHashIv), mpHashProtectingKey as SecretKey)

        return cs.keccak512(passwd).contentEquals(decryptedMPHash)
    }

    fun loginByPIN(PIN: Int, context: Context): Boolean {
        val vaultDao = DatabaseProvider.getDatabase(context).vaultDao()
        val vault = vaultDao.getVault()
        if (vault == emptyList<Vault>())
            return false

        val keystore: KeyStore = KeyStore.getInstance(kg.AKSProviderName).apply { load(null) }
        val pinHashProtectingKey: Key? = keystore.getKey(pinHashProtectingKeyName, null)
            ?: throw RuntimeException("Cannot retrieve the $pinHashProtectingKeyName key! A $pinHashProtectingKeyName key might not have been generated...")

        val decryptedPINHash = ac.decrypt(mapOf(CryptoNames.cipher to vault[0].pinHash, CryptoNames.iv to vault[0].pinHashIv), pinHashProtectingKey as SecretKey)

        return cs.keccak512(PIN.toString()).contentEquals(decryptedPINHash)
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
